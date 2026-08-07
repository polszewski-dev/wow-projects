package wow.character.model.character;

import wow.character.model.build.Build;
import wow.character.model.build.Talents;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.Pet;
import wow.commons.model.character.PetType;
import wow.commons.model.character.Race;
import wow.commons.model.character.RaceId;
import wow.commons.model.effect.RacialEffect;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.Side;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
public interface PlayerCharacter extends Character {
	default void resetBuild() {
		getBuild().reset();
	}

	// buffs

	Buffs getBuffs();

	default void resetBuffs() {
		getBuffs().reset();
	}

	// race

	Race getRace();

	@Override
	default RaceId getRaceId() {
		return getRace().getRaceId();
	}

	@Override
	default Side getSide() {
		return getRace().getSide();
	}

	default List<RacialEffect> getRacials() {
		return getRace().getRacials(this);
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

	@Override
	default boolean hasActivePet(PetType petType) {
		return getActivePetType() == petType;
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

	// build

	Build getBuild();

	@Override
	default boolean hasTalent(String name) {
		return getBuild().hasTalent(name);
	}

	@Override
	default boolean hasTalent(String name, int rank) {
		return getBuild().hasTalent(name, rank);
	}

	default Talents getTalents() {
		return getBuild().getTalents();
	}

	default String getTalentLink() {
		return getTalents().getTalentLink();
	}

	@Override
	default PveRole getRole() {
		return getBuild().getRole();
	}

	default Pet getActivePet() {
		return getBuild().getActivePet();
	}

	@Override
	default PetType getActivePetType() {
		return getActivePet() != null ? getActivePet().getPetType() : null;
	}

	ExclusiveFactions getExclusiveFactions();

	@Override
	default boolean hasExclusiveFaction(String exclusiveFaction) {
		return getExclusiveFactions().has(exclusiveFaction);
	}

	Assets getAssets();
}
