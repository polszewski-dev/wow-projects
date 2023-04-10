package wow.character.model.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.character.CharacterProfession;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.PetType;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.spells.SpellId;

import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
@Getter
public class BuildTemplate implements TimeRestricted {
	private final BuildId buildId;
	private final CharacterClassId characterClassId;
	private final int level;
	private final TimeRestriction timeRestriction;
	private final String talentLink;
	private final PveRole role;
	private final List<SpellId> defaultRotation;
	private final List<SpellId> relevantSpells;
	private final PetType activePet;
	private final Map<BuffSetId, List<String>> buffSets;
	private final List<CharacterProfession> professions;
}
