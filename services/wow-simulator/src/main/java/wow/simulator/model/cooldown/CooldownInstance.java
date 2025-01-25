package wow.simulator.model.cooldown;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.spell.CooldownId;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.unit.action.UnitAction;
import wow.simulator.model.unit.impl.UnitImpl;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;
import wow.simulator.util.IdGenerator;

import static wow.commons.model.spell.GcdCooldownId.GCD;

/**
 * User: POlszewski
 * Date: 2023-08-19
 */
public class CooldownInstance extends Action implements SimulationContextSource {
	private static final IdGenerator<CooldownInstanceId> ID_GENERATOR = new IdGenerator<>(CooldownInstanceId::new);

	@Getter
	protected final CooldownInstanceId id = ID_GENERATOR.newId();
	@Getter
	private final CooldownId cooldownId;
	@Getter
	private final Unit owner;
	@Getter
	private final Duration duration;
	private final Time endTime;

	private final UnitAction sourceAction;

	public CooldownInstance(CooldownId cooldownId, Unit owner, Duration duration, UnitAction sourceAction) {
		super(owner.getClock());
		this.cooldownId = cooldownId;
		this.owner = owner;
		this.duration = duration;
		this.endTime = now().add(duration);
		this.sourceAction = sourceAction;
	}

	@Override
	protected void setUp() {
		on(endTime, () -> {});
	}

	@Override
	protected void onStarted() {
		if (cooldownId == GCD) {
			getGameLog().beginGcd(sourceAction);
		} else {
			getGameLog().cooldownStarted(this);
		}
	}

	@Override
	protected void onFinished() {
		((UnitImpl) owner).detach(this);

		if (cooldownId == GCD) {
			getGameLog().endGcd(sourceAction);
		} else {
			getGameLog().cooldownExpired(this);
		}
	}

	@Override
	protected void onInterrupted() {
		onFinished();
	}

	public boolean isActive() {
		return getClock().timeInTheFuture(endTime);
	}

	public Duration getRemainingDuration() {
		return endTime.subtract(now()).min(Duration.ZERO);
	}

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}
}
