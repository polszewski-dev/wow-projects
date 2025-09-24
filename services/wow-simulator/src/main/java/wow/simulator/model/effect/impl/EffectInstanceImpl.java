package wow.simulator.model.effect.impl;

import wow.commons.model.AnyDuration;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.*;
import wow.commons.model.effect.component.*;
import wow.commons.model.spell.*;
import wow.commons.model.talent.TalentTree;
import wow.simulator.model.action.Action;
import wow.simulator.model.action.ActionStatus;
import wow.simulator.model.context.Context;
import wow.simulator.model.context.EffectUpdateContext;
import wow.simulator.model.context.EventContext;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.EffectInstanceId;
import wow.simulator.model.time.AnyTime;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.impl.UnitImpl;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.util.IdGenerator;

import java.util.List;

import static wow.commons.util.CollectionUtil.join;

/**
 * User: POlszewski
 * Date: 2023-11-03
 */
public abstract class EffectInstanceImpl extends Action implements EffectInstance {
	private static final IdGenerator<EffectInstanceId> ID_GENERATOR = new IdGenerator<>(EffectInstanceId::new);

	protected final EffectInstanceId instanceId = ID_GENERATOR.newId();

	protected final Unit owner;
	protected final Unit target;

	protected final Effect effect;
	protected final AnyDuration duration;

	protected int numStacks;
	protected int numCharges;

	private final EffectSource effectSource;
	private final Spell sourceSpell;

	protected final EffectUpdateContext effectUpdateContext;

	protected AnyTime endTime;

	private boolean silentRemoval = false;
	private boolean stacked = false;
	private boolean removed;
	private boolean fireStacksMaxed;

	private Runnable onEffectFinished;

	private List<Attribute> modifierAttributeList;
	private List<StatConversion> statConversions;
	private List<Event> events;

	protected EffectInstanceImpl(
			Unit owner,
			Unit target,
			Effect effect,
			AnyDuration duration,
			int numStacks,
			int numCharges,
			EffectSource effectSource,
			Spell sourceSpell,
			Context parentContext
	) {
		super(owner.getClock());
		this.owner = owner;
		this.target = target;
		this.effect = effect;
		this.duration = duration;
		this.numStacks = numStacks;
		this.numCharges = numCharges;
		this.effectSource = effectSource;
		this.sourceSpell = sourceSpell;
		this.effectUpdateContext = new EffectUpdateContext(this, parentContext);

		this.modifierAttributeList = effect.getModifierAttributeList();
		this.statConversions = effect.getStatConversions();
		this.events = effect.getEvents();

		if (numStacks > effect.getMaxStacks()) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected final void setUp() {
		endTime = now().add(duration);
		doSetUp();
		checkIfStacksAreMaxed();
	}

	protected abstract void doSetUp();

	@Override
	protected void onStarted() {
		if (stacked) {
			getGameLog().effectStacked(this);
		} else {
			getGameLog().effectApplied(this);
		}
	}

	@Override
	protected void onFinished() {
		this.removed = true;

		getGameLog().effectExpired(this);

		if (onEffectFinished != null) {
			onEffectFinished.run();
		}

		fireEffectEnded();
	}

	@Override
	protected void onInterrupted() {
		this.removed = true;
		getScheduler().rescheduleInterruptedActionToPresent(this, endTime);

		if (!silentRemoval) {
			getGameLog().effectRemoved(this);
			fireEffectEnded();
		}
	}

	@Override
	public void onRemovedFromQueue() {
		super.onRemovedFromQueue();
		if (target != null) {
			((UnitImpl) target).detach(this);
		}
	}

	private void fireEffectEnded() {
		EventContext.fireEffectEnded(this, effectUpdateContext);
	}

	@Override
	public boolean matches(AbilityId abilityId, Unit owner) {
		return getSourceSpell() instanceof Ability a && a.getAbilityId().equals(abilityId) && this.owner == owner;
	}

	@Override
	public boolean matches(AbilityId abilityId) {
		return getSourceSpell() instanceof Ability a && a.getAbilityId().equals(abilityId);
	}

	@Override
	public boolean matches(TalentTree tree) {
		return getSourceSpell() instanceof ClassAbility a && a.getTalentTree() == tree;
	}

	@Override
	public EffectInstanceId getInstanceId() {
		return instanceId;
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

	public void stack(EffectInstance existingEffect) {
		if (removed || getMaxStacks() == 1) {
			return;
		}
		this.stacked = true;
		this.addStacks(existingEffect.getNumStacks());
		((EffectInstanceImpl) existingEffect).silentRemoval = true;
	}

	@Override
	public void addStack() {
		if (removed) {
			return;
		}
		addStacks(1);
		endTime = now().add(duration);
		getGameLog().effectStacksIncreased(this);
	}

	private void addStacks(int stacksToAdd) {
		numStacks = Math.min(numStacks + stacksToAdd, getMaxStacks());
		checkIfStacksAreMaxed();
	}

	private void checkIfStacksAreMaxed() {
		if (effect.getMaxStacks() > 1 && numStacks == effect.getMaxStacks() && !silentRemoval) {
			fireStacksMaxed();
		}
	}

	private void fireStacksMaxed() {
		if (getStatus() == ActionStatus.CREATED) {
			this.fireStacksMaxed = true;
		} else if (getStatus() == ActionStatus.IN_PROGRESS) {
			EventContext.fireStacksMaxed(this, effectUpdateContext);
		}
	}

	public void fireDeferredEvents() {
		if (fireStacksMaxed) {
			EventContext.fireStacksMaxed(this, effectUpdateContext);
		}
	}

	@Override
	public void removeStack() {
		if (--numStacks <= 0) {
			removeSelf();
		} else {
			getGameLog().effectStacksDecreased(this);
		}
	}

	@Override
	public void addCharge() {
		++numCharges;
		getGameLog().effectChargesIncreased(this);
	}

	@Override
	public void removeCharge() {
		if (--numCharges <= 0) {
			removeSelf();
		} else {
			getGameLog().effectChargesDecreased(this);
		}
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
	public AnyDuration getRemainingDuration() {
		return getRemainingDurationUntil(endTime);
	}

	@Override
	public AnyDuration getDuration() {
		return duration;
	}

	@Override
	public int getNumStacks() {
		return numStacks;
	}

	@Override
	public int getNumCharges() {
		return numCharges;
	}

	// effect interface

	@Override
	public EffectId getId() {
		return effect.getId();
	}

	@Override
	public EffectCategory getCategory() {
		return effect.getCategory();
	}

	@Override
	public EffectSource getSource() {
		return effectSource;
	}

	@Override
	public List<AbilityId> getAugmentedAbilities() {
		return effect.getAugmentedAbilities();
	}

	@Override
	public int getMaxStacks() {
		return effect.getMaxStacks();
	}

	@Override
	public EffectScope getScope() {
		return effect.getScope();
	}

	@Override
	public EffectExclusionGroup getExclusionGroup() {
		return effect.getExclusionGroup();
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
		return modifierAttributeList;
	}

	@Override
	public AbsorptionComponent getAbsorptionComponent() {
		return effect.getAbsorptionComponent();
	}

	@Override
	public List<SpellSchool> getPreventedSchools() {
		return effect.getPreventedSchools();
	}

	@Override
	public List<StatConversion> getStatConversions() {
		return statConversions;
	}

	@Override
	public List<Event> getEvents() {
		return events;
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

	@Override
	public void augment(EffectAugmentations augmentations) {
		addModifiers(augmentations.modifiers());
		addStatConversions(augmentations.statConversions());
		addEvents(augmentations.events());
		addEffectIncrease(augmentations.effectIncreasePct());
	}

	private void addEffectIncrease(double effectIncreasePct) {
		this.modifierAttributeList = getScaledAttributes(effectIncreasePct);
	}

	private List<Attribute> getScaledAttributes(double effectIncreasePct) {
		if (effectIncreasePct == 0 || modifierAttributeList == null) {
			return modifierAttributeList;
		}

		var factor = 1 + effectIncreasePct / 100.0;

		return modifierAttributeList.stream()
				.map(x -> x.intScale(factor))
				.toList();
	}

	private void addModifiers(List<Attribute> extraModifiers) {
		this.modifierAttributeList = join(modifierAttributeList, extraModifiers);
	}

	private void addStatConversions(List<StatConversion> extraStatConversions) {
		this.statConversions = join(statConversions, extraStatConversions);
	}

	private void addEvents(List<Event> extraEvents) {
		this.events = join(events, extraEvents);
	}

	@Override
	public void increaseEffect(double effectIncreasePct) {
		effectUpdateContext.increaseEffect(effectIncreasePct);
	}
}
