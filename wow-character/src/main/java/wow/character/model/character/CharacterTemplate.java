package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.PetType;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.spells.SpellId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
@Getter
public class CharacterTemplate implements TimeRestricted {
	private final CharacterTemplateId characterTemplateId;
	private final CharacterClassId characterClassId;
	private final int level;
	private final TimeRestriction timeRestriction;
	private final String talentLink;
	private final PveRole role;
	private final List<SpellId> defaultRotation;
	private final PetType activePet;
	private final List<String> defaultBuffs;
	private final List<String> defaultDebuffs;
	private final List<CharacterProfession> professions;
	private final List<ExclusiveFaction> exclusiveFactions;
}
