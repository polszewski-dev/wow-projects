package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.build.Build;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.ItemSubType;
import wow.commons.model.categorization.ItemType;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.PetType;
import wow.commons.model.character.Race;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.talents.TalentId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@AllArgsConstructor
@Getter
public class CharacterInfo implements wow.commons.model.config.CharacterInfo {
	private final CharacterClass characterClass;
	private final Race race;
	private final int level;
	private final Build build;
	private final CharacterProfessions characterProfessions;
	private final Phase phase;
	private final BaseStatInfo baseStatInfo;
	private final CombatRatingInfo combatRatingInfo;

	public GameVersion getGameVersion() {
		return phase.getGameVersion();
	}

	public Side getSide() {
		return race.getSide();
	}

	public List<CharacterProfession> getProfessions() {
		return characterProfessions.getProfessions();
	}

	public boolean hasProfession(Profession profession) {
		return characterProfessions.hasProfession(profession);
	}

	@Override
	public boolean hasProfession(Profession profession, int level) {
		return characterProfessions.hasProfession(profession, level);
	}

	@Override
	public boolean hasProfessionSpecialization(ProfessionSpecialization specialization) {
		return characterProfessions.hasProfessionSpecialization(specialization);
	}

	@Override
	public boolean hasTalent(TalentId talentId) {
		return build.hasTalent(talentId);
	}

	public PetType getActivePet() {
		return build.getActivePet();
	}

	public boolean canEquip(ItemSlot itemSlot, ItemType itemType, ItemSubType itemSubType) {
		return characterClass.canEquip(itemSlot, itemType, itemSubType);
	}

	public CharacterInfo setBuild(Build build) {
		if (build == this.build) {
			return this;
		}
		return new CharacterInfo(
				characterClass,
				race,
				level,
				build,
				characterProfessions,
				phase,
				baseStatInfo,
				combatRatingInfo
		);
	}

	public CharacterInfo setPhase(Phase phase) {
		if (phase == this.phase) {
			return this;
		}
		return new CharacterInfo(
				characterClass,
				race,
				level,
				build,
				characterProfessions,
				phase,
				baseStatInfo,
				combatRatingInfo
		);
	}

	public CharacterInfo setBaseStatInfo(BaseStatInfo baseStatInfo) {
		if (baseStatInfo == this.baseStatInfo) {
			return this;
		}
		return new CharacterInfo(
				characterClass,
				race,
				level,
				build,
				characterProfessions,
				phase,
				baseStatInfo,
				combatRatingInfo
		);
	}

	public CharacterInfo setCombatRatingInfo(CombatRatingInfo combatRatingInfo) {
		if (combatRatingInfo == this.combatRatingInfo) {
			return this;
		}
		return new CharacterInfo(
				characterClass,
				race,
				level,
				build,
				characterProfessions,
				phase,
				baseStatInfo,
				combatRatingInfo
		);
	}
}
