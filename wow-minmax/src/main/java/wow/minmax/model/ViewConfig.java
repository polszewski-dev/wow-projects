package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.spells.SpellId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
@Data
@AllArgsConstructor
public class ViewConfig {
	private CharacterClassId characterClassId;
	private PveRole pveRole;
	private GameVersionId gameVersionId;
	private List<SpellId> relevantSpells;
}
