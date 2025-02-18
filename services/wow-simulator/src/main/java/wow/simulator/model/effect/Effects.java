package wow.simulator.model.effect;

import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.EffectReplacementMode;
import wow.commons.model.talent.TalentTree;
import wow.simulator.model.effect.impl.EffectInstanceImpl;
import wow.simulator.model.unit.Unit;
import wow.simulator.simulation.SimulationContext;
import wow.simulator.simulation.SimulationContextSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static wow.commons.model.effect.EffectScope.GLOBAL;

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
			default -> throw new IllegalArgumentException();
		}
	}

	private EffectInstance getMatchingEffect(EffectInstance newEffect) {
		return getEffectInstanceStream()
				.filter(existingEffect -> isMatching(existingEffect, newEffect))
				.findAny()
				.orElse(null);
	}

	private boolean isMatching(EffectInstance existingEffect, EffectInstance newEffect) {
		if (haveTheSameOwner(existingEffect, newEffect)) {
			return haveTheSameName(existingEffect, newEffect) || haveTheSameExclusionGroup(existingEffect, newEffect);
		}

		return newEffect.getScope() == GLOBAL && haveTheSameName(existingEffect, newEffect);
	}

	private static boolean haveTheSameOwner(EffectInstance existing, EffectInstance applied) {
		return existing.getOwner() == applied.getOwner();
	}

	private static boolean haveTheSameName(EffectInstance first, EffectInstance second) {
		return first.getName().equals(second.getName());
	}

	private boolean haveTheSameExclusionGroup(EffectInstance first, EffectInstance second) {
		return first.getExclusionGroup() != null && first.getExclusionGroup() == second.getExclusionGroup();
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

	public int getNumberOfEffects(TalentTree tree) {
		return (int) getEffectInstanceStream()
				.filter(x -> x.matches(tree))
				.count();
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
