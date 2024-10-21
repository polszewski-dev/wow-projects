package wow.scraper.classifier;

import wow.commons.model.categorization.PveRole;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.ActivatedAbility;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
public interface PveRoleStatClassifier {
	PveRole getRole();

	boolean hasStatsSuitableForRole(List<Effect> effects, ActivatedAbility activatedAbility);

	List<PveRoleStatClassifier> CLASSIFIERS = List.of(
			new CasterDpsStatClassifier()
	);

	static List<PveRole> classify(List<Effect> effects, ActivatedAbility activatedAbility) {
		return CLASSIFIERS.stream()
				.filter(x -> x.hasStatsSuitableForRole(effects, activatedAbility))
				.map(PveRoleStatClassifier::getRole)
				.distinct()
				.toList();
	}
}
