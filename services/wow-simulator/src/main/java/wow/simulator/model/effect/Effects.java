package wow.simulator.model.effect;

import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.EffectReplacementMode;
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
import java.util.stream.Stream;

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

	public void addEffect(EffectInstance effect, EffectReplacementMode replacementMode) {
		var effectImpl = (EffectInstanceImpl) effect;
		var matchingEffect = getMatchingEffect(effect);

		if (matchingEffect == null) {
			addNewEffect(effectImpl);
		} else {
			replaceExistingEffect(effectImpl, matchingEffect, replacementMode);
		}
	}

	private void addNewEffect(EffectInstanceImpl effect) {
		var handle = updateQueue.add(effect);

		effect.setHandle(handle);
		effect.fireDeferredEvents();
	}

	private void replaceExistingEffect(EffectInstanceImpl effect, EffectInstance matchingEffect, EffectReplacementMode replacementMode) {
		switch (replacementMode) {
			case DEFAULT -> {
				effect.stack(matchingEffect);
				removeEffect(matchingEffect);
				addNewEffect(effect);
			}
			case ADD_CHARGE ->
				matchingEffect.addCharge();
		}
	}

	private EffectInstance getMatchingEffect(EffectInstance effect) {
		return getEffectInstanceStream()
				.filter(x -> x.getEffectId() == effect.getEffectId())//todo scope
				.filter(x -> x.getOwner() == effect.getOwner())
				.findAny()
				.orElse(null);
	}

	public void removeEffect(EffectInstance effect) {
		updateQueue.remove(((EffectInstanceImpl) effect).getHandle());
	}

	public void removeEffect(AbilityId abilityId, Unit owner) {
		getEffect(abilityId, owner).ifPresent(EffectInstance::removeSelf);
	}

	public void updateAllPresentActions() {
		updateQueue.updateAllPresentActions();
	}

	public Optional<Time> getNextUpdateTime() {
		return updateQueue.getNextUpdateTime();
	}

	public boolean isUnderEffect(AbilityId abilityId, Unit owner) {
		return getEffectInstanceStream()
				.anyMatch(x -> x.matches(abilityId, owner));
	}

	public boolean isUnderEffect(AbilityId abilityId) {
		return getEffectInstanceStream()
				.anyMatch(x -> x.matches(abilityId));
	}

	public Optional<EffectInstance> getEffect(AbilityId abilityId, Unit owner) {
		return getEffectInstanceStream()
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
		if (updateQueue.getCurrentlyUpdatedElement() != null) {
			var effect = updateQueue.getCurrentlyUpdatedElement();
			collector.addEffect(effect, effect.getNumStacks());
		}

		for (var elementHandle : updateQueue.getElements()) {
			var effect = elementHandle.get();
			collector.addEffect(effect, effect.getNumStacks());
		}
	}

	private Stream<EffectInstance> getEffectInstanceStream() {
		return updateQueue.getElements().stream()
				.map(Handle::get);
	}
}
