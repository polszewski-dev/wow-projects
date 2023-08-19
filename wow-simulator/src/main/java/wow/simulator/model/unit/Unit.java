package wow.simulator.model.unit;

import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SnapshotState;
import wow.commons.model.Duration;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.spells.Cost;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.simulator.model.rng.Rng;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.action.CastSpellAction;
import wow.simulator.model.unit.action.IdleAction;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.model.update.UpdateQueue;
import wow.simulator.model.update.Updateable;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextAware;
import wow.simulator.simulation.SimulationContextSource;
import wow.simulator.util.IdGenerator;

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

	private final PendingActionQueue<UnitAction> pendingActionQueue = new PendingActionQueue<>();
	private final UpdateQueue<UnitAction> updateQueue = new UpdateQueue<>();
	private UnitAction currentAction;

	private Consumer<Unit> onPendingActionQueueEmpty;

	private Rng rng;

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
		pendingActionQueue.add(new CastSpellAction(this, spell, getDefaultTarget(spell)));
	}

	public void idleUntil(Time time) {
		pendingActionQueue.add(new IdleAction(this, time));
	}

	public void idleFor(Duration duration) {
		idleUntil(getClock().now().add(duration));
	}

	public boolean canCast(SpellId spellId) {
		Spell spell = getSpell(spellId).orElseThrow();
		return canCast(spell, getDefaultTarget(spell));
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
		return isValidTarget(context) && canPaySpellCost(context);
	}

	private boolean isValidTarget(SpellCastContext context) {
		return switch (context.spell().getTarget()) {
			case SELF -> context.target() == this;
			case PET -> throw new UnsupportedOperationException("No pets atm");
			case FRIEND, FRIENDS_IN_AREA -> context.target() instanceof Player;
			case ENEMY, ENEMIES_IN_AREA -> context.target() instanceof Target;
		};
	}

	private Unit getDefaultTarget(Spell spell) {
		return spell.isFriendly() ? this : target;
	}

	private boolean canPaySpellCost(SpellCastContext context) {
		return resources.canPay(context.cost());
	}

	public void paySpellCost(SpellCastContext context) {
		resources.pay(context.cost(), context.spell());
	}

	private Cost getSpellCost(Snapshot snapshot) {
		return getSpellCost(snapshot, (int) snapshot.getCost());
	}

	private Cost getSpellCostUnreduced(Snapshot snapshot) {
		return getSpellCost(snapshot, (int) snapshot.getCostUnreduced());
	}

	private static Cost getSpellCost(Snapshot snapshot, int cost) {
		Spell spell = snapshot.getSpell();
		return new Cost(spell.getCost().resourceType(), cost);
	}

	public abstract Optional<Spell> getSpell(SpellId spellId);

	public SpellCastContext getSpellCastContext(Spell spell, Unit target) {
		Snapshot snapshot = getSnapshot(spell);
		Cost cost = getSpellCost(snapshot);
		Cost costUnreduced = getSpellCostUnreduced(snapshot);
		return new SpellCastContext(this, target, snapshot, cost, costUnreduced);
	}

	public Snapshot getSnapshot(Spell spell) {
		Snapshot snapshot = getCharacterCalculationService().createSnapshot(((Player) this).getCharacter(), spell, this.getStats());
		getCharacterCalculationService().advanceSnapshot(snapshot, SnapshotState.COMPLETE);
		return snapshot;
	}

	public Rng getRng() {
		if (rng == null) {
			rng = getRngFactory().newRng();
		}
		return rng;
	}

	public int getCurrentHealth() {
		return resources.getCurrentHealth();
	}

	public int getCurrentMana() {
		return resources.getCurrentMana();
	}

	public int increaseHealth(int amount, boolean crit, Spell spell) {
		return resources.increaseHealth(amount, crit, spell);
	}

	public int decreaseHealth(int amount, boolean crit, Spell spell) {
		return resources.decreaseHealth(amount, crit, spell);
	}

	public int increaseMana(int amount, boolean crit, Spell spell) {
		return resources.increaseMana(amount, crit, spell);
	}

	public int decreaseMana(int amount, boolean crit, Spell spell) {
		return resources.decreaseMana(amount, crit, spell);
	}
}
