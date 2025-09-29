package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.build.TalentLink;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.PetType;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.config.TimeRestriction;

import java.util.List;

import static wow.commons.util.CollectionUtil.getUniqueResult;

/**
 * User: POlszewski
 * Date: 2022-11-30
 */
@AllArgsConstructor
@Getter
public class CharacterTemplate implements TimeRestricted, CharacterRestricted {
	private final String name;
	private final CharacterRestriction characterRestriction;
	private final TimeRestriction timeRestriction;
	private final TalentLink talentLink;
	private final String defaultScript;
	private final PetType activePet;
	private final List<String> defaultBuffs;
	private final List<String> defaultDebuffs;
	private final List<String> consumables;
	private final List<ProfIdSpecId> professions;
	private final List<String> exclusiveFactions;
	private final boolean isDefault;

	public CharacterClassId getRequiredCharacterClassId() {
		return getUniqueResult(getRequiredCharacterClassIds()).orElseThrow();
	}

	@Override
	public Integer getRequiredMaxLevel() {
		return characterRestriction.maxLevel() != null
				? characterRestriction.maxLevel()
				: characterRestriction.level();
	}
}
