package wow.scraper.classifiers;

import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.categorization.PveRole;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
public interface PveRoleStatClassifier {
	PveRole getRole();

	boolean hasStatsSuitableForRole(AttributeSource attributeSource);

	List<PveRoleStatClassifier> CLASSIFIERS = List.of(
			new CasterDpsStatClassifier()
	);

	static List<PveRole> classify(AttributeSource attributeSource) {
		return CLASSIFIERS.stream()
				.filter(x -> x.hasStatsSuitableForRole(attributeSource))
				.map(PveRoleStatClassifier::getRole)
				.distinct()
				.toList();
	}
}
