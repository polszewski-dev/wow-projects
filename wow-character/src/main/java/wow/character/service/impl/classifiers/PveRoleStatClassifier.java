package wow.character.service.impl.classifiers;

import wow.character.model.build.PveRole;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.item.Enchant;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
public interface PveRoleStatClassifier {
	PveRole getRole();

	boolean hasStatsSuitableForRole(AttributeSource attributeSource);

	boolean hasStatsSuitableForRole(Enchant enchant, ItemType itemType);
}
