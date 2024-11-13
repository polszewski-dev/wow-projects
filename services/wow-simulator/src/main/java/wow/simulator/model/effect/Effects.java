package wow.simulator.model.effect;

import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.effect.impl.EffectInstanceImpl;
import wow.simulator.model.time.Clock;
import wow.simulator.model.time.Time;
import wow.simulator.model.unit.Unit;
import wow.simulator.model.update.Handle;
import wow.simulator.model.update.UpdateQueue;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;
import wow.simulator.simulation.TimeAware;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public class Effects implements SimulationContextSource, TimeAware, EffectCollection {
	private final Unit owner;
	private final UpdateQueue<EffectInstance> updateQueue = new UpdateQueue<>();

	public Effects(Unit owner) {
		this.owner = owner;
	}

	public void addEffect(EffectInstance effect) {
		var handle = updateQueue.add(effect);
		((EffectInstanceImpl) effect).setHandle(handle);
	}

	public void removeEffect(EffectInstance effect) {
		updateQueue.remove(((EffectInstanceImpl) effect).getHandle());
	}

	public void updateAllPresentActions() {
		updateQueue.updateAllPresentActions();
	}

	public Optional<Time> getNextUpdateTime() {
		return updateQueue.getNextUpdateTime();
	}

	public boolean isUnderEffect(AbilityId abilityId, Unit owner) {
		return updateQueue.getElements().stream()
				.map(Handle::get)
				.anyMatch(x -> x.matches(abilityId, owner));
	}

	public Optional<EffectInstance> getEffect(AbilityId abilityId, Unit owner) {
		return updateQueue.getElements().stream()
				.map(Handle::get)
				.filter(x -> x.matches(abilityId, owner))
				.findAny();
	}

	@Override
	public SimulationContext getSimulationContext() {
		return owner.getSimulationContext();
	}

	@Override
	public void setClock(Clock clock) {
		updateQueue.setClock(clock);
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		for (var elementHandle : updateQueue.getElements()) {
			var effect = elementHandle.get();
			collector.addEffect(effect, effect.getNumStacks());
		}
	}
}
