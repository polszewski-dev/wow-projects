package wow.simulator.model.effect.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.action.Action;
import wow.simulator.model.effect.EffectInstance;
import wow.simulator.model.effect.EffectInstanceId;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Handle;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.util.IdGenerator;

/**
 * User: POlszewski
 * Date: 2023-11-03
 */
public abstract class EffectInstanceImpl extends Action implements EffectInstance {
	private static final IdGenerator<EffectInstanceId> ID_GENERATOR = new IdGenerator<>(EffectInstanceId::new);

	protected final EffectInstanceId id = ID_GENERATOR.newId();

	protected final Unit owner;
	protected final Unit target;

	@Getter
	@Setter
	private Handle<EffectInstance> handle;
	private Runnable onEffectFinished;

	protected EffectInstanceImpl(Unit owner, Unit target) {
		super(owner.getClock());
		this.owner = owner;
		this.target = target;
	}

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
		return getSourceAbilityId() == abilityId && this.owner == owner;
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
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}
}
