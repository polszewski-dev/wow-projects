package wow.character.model.character;

import wow.character.model.Copyable;
import wow.character.model.build.Build;
import wow.character.model.build.Rotation;
import wow.character.model.build.Talents;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.*;
import wow.commons.model.effect.RacialEffect;
import wow.commons.model.item.Item;
import wow.commons.model.profession.ProfessionId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.Side;
import wow.commons.model.talent.TalentId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
public interface PlayerCharacter extends Character, Copyable<PlayerCharacter> {
	Equipment getEquipment();

	default void equip(EquippableItem item, ItemSlot slot) {
		getEquipment().equip(item, slot);
	}

	default void equip(EquippableItem item) {
		getEquipment().equip(item);
	}

	default void setEquipment(Equipment equipment) {
		getEquipment().setEquipment(equipment);
	}

	default EquippableItem getEquippedItem(ItemSlot slot) {
		return getEquipment().get(slot);
	}

	default boolean canEquip(ItemSlot itemSlot, Item item) {
		return getCharacterClass().canEquip(itemSlot, item.getItemType(), item.getItemSubType());
	}

	default void resetBuild() {
		getBuild().reset();
	}

	default void resetEquipment() {
		getEquipment().reset();
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

	@Override
	default CreatureType getCreatureType() {
		return CreatureType.HUMANOID;
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

	default void setProfessions(List<CharacterProfession> professions) {
		getProfessions().setProfessions(professions);
	}

	default void addProfession(ProfessionId professionId, ProfessionSpecializationId specializationId, int level) {
		var profession = getGameVersion().getProfession(professionId).orElseThrow();
		var specialization = specializationId == null ? null : profession.getSpecialization(specializationId).orElseThrow();

		getProfessions().addProfession(profession, specialization, level);
	}

	default void addProfession(ProfessionId professionId, int level) {
		addProfession(professionId, null, level);
	}

	default void resetProfessions() {
		getProfessions().reset();
	}

	// build

	Build getBuild();

	@Override
	default boolean hasTalent(TalentId talentId) {
		return getBuild().hasTalent(talentId);
	}

	default Talents getTalents() {
		return getBuild().getTalents();
	}

	@Override
	default PveRole getRole() {
		return getBuild().getRole();
	}

	default Rotation getRotation() {
		return getBuild().getRotation().compile(this);
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
	default boolean hasExclusiveFaction(ExclusiveFaction exclusiveFaction) {
		return getExclusiveFactions().has(exclusiveFaction);
	}
}
