package wow.simulator.model.effect;

import wow.commons.model.spells.SpellId;
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
public class Effects implements SimulationContextSource, TimeAware {
	private final Unit owner;
	private final UpdateQueue<Effect> updateQueue = new UpdateQueue<>();

	public Effects(Unit owner) {
		this.owner = owner;
	}

	public void addEffect(Effect effect) {
		var handle = updateQueue.add(effect);
		effect.setHandle(handle);
	}

	public void removeEffect(Effect effect) {
		updateQueue.remove(effect.getHandle());
	}

	public void updateAllPresentActions() {
		updateQueue.updateAllPresentActions();
	}

	public Optional<Time> getNextUpdateTime() {
		return updateQueue.getNextUpdateTime();
	}

	public boolean isUnderEffect(SpellId spellId, Unit owner) {
		return updateQueue.getElements().stream()
				.map(Handle::get)
				.anyMatch(x -> x.matches(spellId, owner));
	}

	public Optional<Effect> getEffect(SpellId spellId, Unit owner) {
		return updateQueue.getElements().stream()
				.map(Handle::get)
				.filter(x -> x.matches(spellId, owner))
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
}
