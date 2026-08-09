package wow.character.model.character;

import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
public interface PlayerCharacter extends Character {
	// buffs

	Buffs getBuffs();

	default void resetBuffs() {
		getBuffs().reset();
	}

	// professions

	CharacterProfessions getProfessions();

	@Override
	default boolean hasProfession(ProfessionId professionId) {
		return getProfessions().hasProfession(professionId);
	}

	@Override
	default boolean hasProfession(ProfessionId professionId, int level) {
		return getProfessions().hasProfession(professionId, level);
	}

	@Override
	default boolean hasProfessionSpecialization(ProfessionSpecializationId specializationId) {
		return getProfessions().hasProfessionSpecialization(specializationId);
	}

	default void addProfession(ProfessionId professionId, ProfessionSpecializationId specializationId, int level) {
		getProfessions().add(professionId, specializationId, level);
	}

	default void addProfession(ProfessionId professionId, int level) {
		getProfessions().add(professionId, level);
	}

	default void addProfessionMaxLevel(ProfessionId professionId, ProfessionSpecializationId specializationId) {
		getProfessions().addMaxLevel(professionId, specializationId);
	}

	default void setProfessions(List<ProfIdSpecIdLevel> professions) {
		getProfessions().set(professions);
	}

	default void setProfessionMaxLevels(List<ProfIdSpecId> professions) {
		getProfessions().setMaxLevels(professions);
	}

	default void setProfessionMaxLevel(int index, ProfIdSpecId profession) {
		getProfessions().setMaxLevel(index, profession);
	}

	default void resetProfessions() {
		getProfessions().reset();
	}

	// other

	ExclusiveFactions getExclusiveFactions();

	@Override
	default boolean hasExclusiveFaction(String exclusiveFaction) {
		return getExclusiveFactions().has(exclusiveFaction);
	}

	Assets getAssets();
}
