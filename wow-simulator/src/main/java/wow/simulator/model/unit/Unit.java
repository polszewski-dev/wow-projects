package wow.simulator.model.unit;

import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SnapshotState;
import wow.commons.model.Duration;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.*;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.IdleAction;
import wow.simulator.model.update.UpdateQueue;
import wow.simulator.model.update.Updateable;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextAware;
import wow.simulator.simulation.SimulationContextSource;
import wow.simulator.util.IdGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * User: POlszewski
 * Date: 2023-08-07
 */
public abstract class Unit implements Updateable, SimulationContextSource, SimulationContextAware {
	private static final IdGenerator<UnitId> ID_GENERATOR = new IdGenerator<>(UnitId::new);

	protected final UnitId id = ID_GENERATOR.newId();
	protected final String name;
	protected Unit target;

	protected final UnitResources resources = new UnitResources(this);

	private final PendingActionQueue pendingActionQueue = new PendingActionQueue();
	private final UpdateQueue updateQueue = new UpdateQueue();
	private Action currentAction;

	private Consumer<Unit> onPendingActionQueueEmpty;

	private SimulationContext simulationContext;

	protected Unit(String name) {
		this.name = name;
		this.resources.setHealth(10_000, 10_000);
		this.resources.setMana(10_000, 10_000);
	}

	@Override
	public SimulationContext getSimulationContext() {
		return simulationContext;
	}

	@Override
	public void setSimulationContext(SimulationContext simulationContext) {
		this.simulationContext = simulationContext;
		shareClock(updateQueue);
	}

	@Override
	public void update() {
		ensureCurrentAction();

		updateQueue.updateAllPresentActions();
	}

	private void ensureCurrentAction() {
		if (hasCurrentAction()) {
			return;
		}

		currentAction = null;

		if (pendingActionQueue.isEmpty()) {
			onPendingActionQueueEmpty.accept(this);
			if (pendingActionQueue.isEmpty()) {
				return;
			}
		}

		currentAction = pendingActionQueue.removeEarliestAction();
		currentAction.start();
		updateQueue.add(currentAction);
	}

	private boolean hasCurrentAction() {
		return currentAction != null && !currentAction.isTerminated();
	}

	@Override
	public Optional<Time> getNextUpdateTime() {
		if (!updateQueue.isEmpty()) {
			return updateQueue.getNextUpdateTime();
		}
		// force onPendingActionQueueEmpty call
		return Optional.of(getClock().now());
	}

	public UnitId getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public abstract Attributes getStats();

	public Unit getTarget() {
		return target;
	}

	public void setTarget(Unit target) {
		this.target = target;
	}

	@Override
	public String toString() {
		return name;
	}

	public void setOnPendingActionQueueEmpty(Consumer<Unit> onPendingActionQueueEmpty) {
		this.onPendingActionQueueEmpty = onPendingActionQueueEmpty;
	}

	public void cast(SpellId spellId) {
		Spell spell = getSpell(spellId).orElseThrow();
		pendingActionQueue.add(new CastSpellAction(this, spell, getDefaultTarget(spellId)));
	}

	public void idleUntil(Time time) {
		pendingActionQueue.add(new IdleAction(this, time));
	}

	public void idleFor(Duration duration) {
		idleUntil(getClock().now().add(duration));
	}

	public boolean canCast(SpellId spellId) {
		return canCast(spellId, getDefaultTarget(spellId));
	}

	private Unit getDefaultTarget(SpellId spellId) {
		Spell spell = getSpell(spellId).orElseThrow();
		return spell.hasDamageComponent() ? target : this;
	}

	public boolean canCast(SpellId spellId, Unit target) {
		Spell spell = getSpell(spellId).orElseThrow();
		return canCast(spell, target);
	}

	public boolean canCast(Spell spell, Unit target) {
		SpellCastContext context = getSpellCastContext(spell, target);
		return canCast(context);
	}

	public boolean canCast(SpellCastContext context) {
		return canPaySpellCosts(context);
	}

	private boolean canPaySpellCosts(SpellCastContext context) {
		return resources.canPay(context.costs());
	}

	public void paySpellCosts(SpellCastContext context) {
		resources.pay(context.costs(), context.spell());
	}

	private List<Cost> getSpellCosts(Snapshot snapshot) {
		Spell spell = snapshot.getSpell();
		List<Cost> result = new ArrayList<>();

		int manaCost = spell.getManaCost();

		if (manaCost > 0) {
			result.add(new Cost(ResourceType.MANA, (int)snapshot.getManaCost()));
		}

		AdditionalCost additionalCost = spell.getAdditionalCost();

		if (additionalCost != null) {
			result.add(getAdditionalCost(additionalCost));
		}

		return result;
	}

	private Cost getAdditionalCost(AdditionalCost additionalCost) {
		int costAmount;
		if (additionalCost.scaled()) {
			costAmount = getScaledCost(additionalCost.amount());
		} else {
			costAmount = additionalCost.amount();
		}
		return new Cost(additionalCost.resourceType(), costAmount);
	}

	private int getScaledCost(int baseCost) {
		return baseCost;
	}

	public abstract Optional<Spell> getSpell(SpellId spellId);

	public void delayedAction(Duration delay, Runnable handler) {
		if (delay.isZero()) {
			handler.run();
			return;
		}
		Action action = new Action(getClock()) {
			@Override
			protected void setUp() {
				fromNowAfter(delay, handler);
			}
		};
		action.start();
		updateQueue.add(action);
	}

	public SpellCastContext getSpellCastContext(Spell spell, Unit target) {
		Snapshot snapshot = getSnapshot(spell);
		List<Cost> costs = getSpellCosts(snapshot);
		return new SpellCastContext(this, target, snapshot, costs);
	}

	public Snapshot getSnapshot(Spell spell) {
		Snapshot snapshot = getCharacterCalculationService().createSnapshot(((Player) this).getCharacter(), spell, this.getStats());
		getCharacterCalculationService().advanceSnapshot(snapshot, SnapshotState.COMPLETE);
		return snapshot;
	}

	public int getCurrentHealth() {
		return resources.getCurrentHealth();
	}

	public int getCurrentMana() {
		return resources.getCurrentMana();
	}

	public void increaseHealth(int amount, Spell spell) {
		resources.increaseHealth(amount, spell);
	}

	public void decreaseHealth(int amount, Spell spell) {
		resources.decreaseHealth(amount, spell);
	}

	public void increaseMana(int amount, Spell spell) {
		resources.increaseMana(amount, spell);
	}

	public void decreaseMana(int amount, Spell spell) {
		resources.decreaseMana(amount, spell);
	}
}
