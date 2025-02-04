package wow.simulator.model.effect;

import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.EffectReplacementMode;
import wow.simulator.model.effect.impl.EffectInstanceImpl;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-08-16
 */
public class Effects implements SimulationContextSource, EffectCollection {
	private final Unit owner;

	private final Map<EffectInstanceId, EffectInstance> effectsById = new HashMap<>();

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
		attach(effect);

		getScheduler().add(effect);

		effect.fireDeferredEvents();
	}

	private void replaceExistingEffect(EffectInstanceImpl effect, EffectInstance matchingEffect, EffectReplacementMode replacementMode) {
		switch (replacementMode) {
			case DEFAULT -> {
				effect.stack(matchingEffect);
				hardRemoveEffect(matchingEffect);
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
		((EffectInstanceImpl) effect).interrupt();
	}

	private void hardRemoveEffect(EffectInstance effect) {
		detach(effect);
		((EffectInstanceImpl) effect).interrupt();
	}

	public void removeEffect(AbilityId abilityId, Unit owner) {
		getEffect(abilityId, owner).ifPresent(this::removeEffect);
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
	public void collectEffects(EffectCollector collector) {
		for (var effect : effectsById.values()) {
			collector.addEffect(effect, effect.getNumStacks());
		}
	}

	private Stream<EffectInstance> getEffectInstanceStream() {
		return effectsById.values().stream();
	}

	private void attach(EffectInstanceImpl effect) {
		effectsById.put(effect.getId(), effect);
	}

	public void detach(EffectInstance effect) {
		this.effectsById.remove(effect.getId());
	}
}
