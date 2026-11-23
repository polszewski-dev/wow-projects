package wow.character.service.impl.calc;

import wow.character.model.character.Character;
import wow.character.model.snapshot.AccumulatedPartialStats;
import wow.character.util.AbstractEffectCollector;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.StatConversion;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * User: POlszewski
 * Date: 2026-11-24
 */
class DefaultEffectCollector extends AbstractEffectCollector.OnlyEffects {
	final AccumulatedPartialStats stats;
	final Predicate<Attribute> attributePredicate;
	List<StatConversion> statConversions;

	DefaultEffectCollector(Character character, Predicate<Attribute> attributePredicate, AccumulatedPartialStats stats) {
		super(character);
		this.stats = stats;
		this.attributePredicate = attributePredicate;
	}

	@Override
	public void addEffect(Effect effect, int numStacks) {
		if (effect.hasAugmentedAbilities()) {
			return;
		}

		if (effect.hasModifierComponent()) {
			var modifierAttributeList = effect.getModifierAttributeList();
			stats.accumulateAttributes(modifierAttributeList, numStacks, attributePredicate);
		}

		if (effect.hasStatConversions()) {
			addStatConversions(effect);
		}
	}

	private void addStatConversions(Effect effect) {
		if (statConversions == null) {
			statConversions = new ArrayList<>();
		}

		statConversions.addAll(effect.getStatConversions());
	}

	void solveStatConversions() {
		if (statConversions == null) {
			return;
		}

		var solver = new StatConversionSolver(character, stats, statConversions);

		solver.solve();
	}
}
