package wow.estimator.model;

import lombok.NoArgsConstructor;
import wow.character.model.Copyable;
import wow.character.model.effect.EffectCollection;
import wow.character.model.effect.EffectCollector;
import wow.commons.model.spell.SpellSchool;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * User: POlszewski
 * Date: 2026-03-24
 */
@NoArgsConstructor
public class EffectInstances implements Copyable<EffectInstances>, EffectCollection {
	private final List<EffectInstance> list = new ArrayList<>();

	public EffectInstances(EffectInstances effectInstances) {
		list.addAll(effectInstances.list);
	}

	@Override
	public EffectInstances copy() {
		return new EffectInstances(this);
	}

	@Override
	public void collectEffects(EffectCollector collector) {
		for (var effectInstance : list) {
			collector.addEffect(effectInstance.effect(), effectInstance.numStacks());
		}
	}

	public void add(EffectInstance effectInstance) {
		list.add(effectInstance);
	}

	public void reset() {
		list.clear();
	}

	public void removeIf(Predicate<EffectInstance> predicate) {
		list.removeIf(predicate);
	}

	public boolean isSchoolPrevented(SpellSchool school) {
		return list.stream().anyMatch(effectInstance -> effectInstance.isSchoolPrevented(school));
	}

	public Optional<EffectInstance> get(String effectName) {
		return list.stream()
				.filter(effectInstance -> effectInstance.effect().getName().equals(effectName))
				.findFirst();
	}
}
