package wow.simulator.model.effect;

import wow.commons.model.Duration;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellId;
import wow.simulator.model.action.Action;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Handle;
import wow.simulator.model.update.Updateable;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;
import wow.simulator.util.IdGenerator;

import java.util.Optional;
import java.util.function.IntConsumer;

/**
 * User: POlszewski
 * Date: 2023-08-17
 */
public abstract class Effect implements Updateable, SimulationContextSource {
	private static final IdGenerator<EffectId> ID_GENERATOR = new IdGenerator<>(EffectId::new);

	protected final EffectId effectId = ID_GENERATOR.newId();

	protected final Unit owner;
	protected final Unit target;

	private final EffectAction effectAction;
	private Handle<Effect> handle;

	protected Effect(Unit owner, Unit target) {
		this.owner = owner;
		this.target = target;
		this.effectAction = new EffectAction(owner.getClock());
	}

	protected abstract void setUp();

	@Override
	public void update() {
		effectAction.update();
	}

	@Override
	public Optional<Time> getNextUpdateTime() {
		return effectAction.getNextUpdateTime();
	}

	@Override
	public void onAddedToQueue() {
		effectAction.start();
		getGameLog().effectApplied(this);
	}

	@Override
	public void onRemovedFromQueue() {
		effectAction.interrupt();
	}

	protected void onFinished() {
		getGameLog().effectExpired(this);
	}

	protected void onInterrupted() {
		getGameLog().effectRemoved(this);
	}

	protected void fromNowAfter(Duration duration, Runnable action) {
		effectAction.fromNowAfter(duration, action);
	}

	protected Time fromNowOnEachTick(int numTicks, Duration tickInterval, IntConsumer action) {
		return effectAction.fromNowOnEachTick(numTicks, tickInterval, action);
	}

	public boolean matches(SpellId spellId, Unit owner) {
		return getSourceSpell().getSpellId() == spellId && this.owner == owner;
	}

	private class EffectAction extends Action {
		public EffectAction(Clock clock) {
			super(clock);
		}

		@Override
		protected void setUp() {
			Effect.this.setUp();
		}

		@Override
		protected void onFinished() {
			Effect.this.onFinished();
		}

		@Override
		protected void onInterrupted() {
			Effect.this.onInterrupted();
		}

		@Override
		public void fromNowAfter(Duration duration, Runnable action) {
			super.fromNowAfter(duration, action);
		}

		@Override
		public Time fromNowOnEachTick(int numTicks, Duration tickInterval, IntConsumer action) {
			return super.fromNowOnEachTick(numTicks, tickInterval, action);
		}
	}

	public EffectId getEffectId() {
		return effectId;
	}

	public Unit getOwner() {
		return owner;
	}

	public Unit getTarget() {
		return target;
	}

	public abstract Spell getSourceSpell();

	public abstract Duration getRemainingDuration();

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}

	Handle<Effect> getHandle() {
		return handle;
	}

	void setHandle(Handle<Effect> handle) {
		this.handle = handle;
	}

	@Override
	public String toString() {
		return getSourceSpell().toString();
	}
}
