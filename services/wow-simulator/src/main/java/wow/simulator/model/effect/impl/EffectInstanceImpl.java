package wow.simulator.model.effect.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.Duration;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectCategory;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.effect.component.*;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.Conversion;
import wow.commons.model.spell.Spell;
import wow.simulator.model.action.Action;
import wow.simulator.model.context.EffectUpdateContext;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.EffectInstanceId;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Handle;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.util.IdGenerator;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-11-03
 */
public abstract class EffectInstanceImpl extends Action implements EffectInstance {
	private static final IdGenerator<EffectInstanceId> ID_GENERATOR = new IdGenerator<>(EffectInstanceId::new);

	protected final EffectInstanceId id = ID_GENERATOR.newId();

	protected final Unit owner;
	protected final Unit target;

	protected final Effect effect;
	protected final Duration duration;

	protected int numStacks;
	protected int numCharges;

	protected final Spell sourceSpell;
	protected final EffectUpdateContext resolutionContext;

	protected Time endTime;

	@Getter
	@Setter
	private Handle<EffectInstance> handle;
	private Runnable onEffectFinished;

	protected EffectInstanceImpl(
			Unit owner,
			Unit target,
			Effect effect,
			Spell sourceSpell,
			Duration duration,
			int numStacks,
			int numCharges
	) {
		super(owner.getClock());
		this.owner = owner;
		this.target = target;
		this.effect = effect;
		this.sourceSpell = sourceSpell;
		this.duration = duration;
		this.numStacks = numStacks;
		this.numCharges = numCharges;
		this.resolutionContext = new EffectUpdateContext(owner, this);

		if (numStacks > effect.getMaxStacks()) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected final void setUp() {
		endTime = now().add(duration);
		doSetUp();
	}

	protected abstract void doSetUp();

	@Override
	public void onAddedToQueue() {
		super.onAddedToQueue();
		getGameLog().effectApplied(this);
	}

	@Override
	protected void onFinished() {
		super.onFinished();
		getGameLog().effectExpired(this);
		if (onEffectFinished != null) {
			onEffectFinished.run();
		}
	}

	@Override
	protected void onInterrupted() {
		super.onInterrupted();
		getGameLog().effectRemoved(this);
	}

	@Override
	public boolean matches(AbilityId abilityId, Unit owner) {
		return getSourceSpell() instanceof Ability a && a.getAbilityId() == abilityId && this.owner == owner;
	}

	@Override
	public EffectInstanceId getId() {
		return id;
	}

	@Override
	public Unit getOwner() {
		return owner;
	}

	@Override
	public Unit getTarget() {
		return target;
	}

	@Override
	public void removeSelf() {
		target.removeEffect(this);
	}

	@Override
	public void setOnEffectFinished(Runnable onEffectFinished) {
		this.onEffectFinished = onEffectFinished;
	}

	@Override
	public Spell getSourceSpell() {
		return sourceSpell;
	}

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}

	@Override
	public Duration getRemainingDuration() {
		return endTime.subtract(now()).max(Duration.ZERO);
	}

	@Override
	public Duration getDuration() {
		return duration;
	}

	@Override
	public int getNumStacks() {
		return numStacks;
	}

	// effect interface

	@Override
	public int getEffectId() {
		return effect.getEffectId();
	}

	@Override
	public EffectCategory getCategory() {
		return effect.getCategory();
	}

	@Override
	public EffectSource getSource() {
		return effect.getSource();
	}

	@Override
	public AbilityId getAugmentedAbility() {
		return effect.getAugmentedAbility();
	}

	@Override
	public int getMaxStacks() {
		return effect.getMaxStacks();
	}

	@Override
	public PeriodicComponent getPeriodicComponent() {
		return effect.getPeriodicComponent();
	}

	@Override
	public ModifierComponent getModifierComponent() {
		return effect.getModifierComponent();
	}

	@Override
	public List<Attribute> getModifierAttributeList() {
		return effect.getModifierAttributeList();
	}

	@Override
	public AbsorptionComponent getAbsorptionComponent() {
		return effect.getAbsorptionComponent();
	}

	@Override
	public Conversion getConversion() {
		return effect.getConversion();
	}

	@Override
	public List<StatConversion> getStatConversions() {
		return effect.getStatConversions();
	}

	@Override
	public EffectIncreasePerEffectOnTarget getEffectIncreasePerEffectOnTarget() {
		return effect.getEffectIncreasePerEffectOnTarget();
	}

	@Override
	public List<Event> getEvents() {
		return effect.getEvents();
	}

	@Override
	public Description getDescription() {
		return effect.getDescription();
	}

	@Override
	public TimeRestriction getTimeRestriction() {
		return effect.getTimeRestriction();
	}

	@Override
	public String toString() {
		return effect.toString();
	}
}
