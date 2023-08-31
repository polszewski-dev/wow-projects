package wow.simulator.model.effect.impl;

import wow.commons.model.Duration;
import wow.commons.model.attribute.primitive.PrimitiveAttribute;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.EffectCategory;
import wow.commons.model.effect.EffectSource;
import wow.commons.model.effect.component.*;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.Conversion;
import wow.simulator.model.context.EffectUpdateContext;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-11-01
 */
public class TickingEffect extends UnitEffectImpl {
	private final Effect effect;
	private final Ability sourceAbility;
	private final Duration duration;
	private final Duration tickInterval;
	private Time endTime;
	private int tickNo;
	private int numStacks;
	private int numCharges;

	private final EffectUpdateContext resolutionContext;

	public TickingEffect(
			Unit owner,
			Unit target,
			Effect effect,
			Ability sourceAbility,
			Duration duration,
			Duration tickInterval,
			int numStacks,
			int numCharges
	) {
		super(owner, target);
		this.effect = effect;
		this.sourceAbility = sourceAbility;
		this.duration = duration;
		this.tickInterval = tickInterval;
		this.numStacks = numStacks;
		this.numCharges = numCharges;
		this.resolutionContext = new EffectUpdateContext(owner, this);

		if (numStacks > effect.getMaxStacks()) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected void setUp() {
		endTime = now().add(duration);

		scheduleNextTick();
	}

	private void tick() {
		++tickNo;

		var periodicComponent = effect.getPeriodicComponent();

		if (periodicComponent != null) {
			execPeriodicComponent(periodicComponent);
		}

		// todo fire tick event!!!

		scheduleNextTick();
	}

	private void execPeriodicComponent(PeriodicComponent periodicComponent) {
		switch (periodicComponent.type()) {
			case DAMAGE ->
					resolutionContext.dealPeriodicDamage(tickNo, numStacks);
			default ->
					throw new UnsupportedOperationException();
		}
	}

	private void scheduleNextTick() {
		if (tickInterval != null && getRemainingDuration().isPositive()) {
			fromNowAfter(tickInterval, this::tick);
		}
	}

	@Override
	public Ability getSourceAbility() {
		return sourceAbility;
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
	public List<PrimitiveAttribute> getModifierAttributeList() {
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
	public Duration getTickInterval() {
		return tickInterval;
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
