package wow.character.model.build;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.character.CharacterClass;
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
	private final CharacterClass characterClass;
	private final int level;
	private final TimeRestriction timeRestriction;
	private final String talentLink;
	private final PveRole role;
	private final SpellId damagingSpell;
	private final List<SpellId> relevantSpells;
	private final PetType activePet;
	private final Map<BuffSetId, List<String>> buffSets;
}