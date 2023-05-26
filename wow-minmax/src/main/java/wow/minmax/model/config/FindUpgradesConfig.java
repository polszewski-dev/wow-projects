package wow.minmax.model.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.item.Enchant;
import wow.commons.model.pve.GameVersionId;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-05-26
 */
@Data
@AllArgsConstructor
public class FindUpgradesConfig {
	private CharacterClassId characterClassId;
	private PveRole pveRole;
	private GameVersionId gameVersionId;
	private Set<String> enchantNames;

	public boolean isIncluded(Enchant x) {
		return enchantNames.contains(x.getName());
	}
}
