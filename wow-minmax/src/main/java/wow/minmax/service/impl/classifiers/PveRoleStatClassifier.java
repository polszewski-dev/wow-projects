package wow.minmax.service.impl.classifiers;

import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.character.PveRole;
import wow.commons.model.item.Enchant;
import wow.minmax.model.PlayerProfile;

/**
 * User: POlszewski
 * Date: 2022-12-19
 */
public interface PveRoleStatClassifier {
	PveRole getRole();

	boolean hasStatsSuitableForRole(AttributeSource attributeSource, PlayerProfile playerProfile);

	boolean hasStatsSuitableForRole(Enchant enchant, ItemType itemType, PlayerProfile playerProfile);
}
