package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.build.RotationTemplate;
import wow.commons.model.buff.BuffId;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.PetType;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

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
	private final RotationTemplate defaultRotationTemplate;
	private final PetType activePet;
	private final List<BuffId> defaultBuffs;
	private final List<BuffId> defaultDebuffs;
	private final List<CharacterProfession> professions;
	private final List<ExclusiveFaction> exclusiveFactions;
}
