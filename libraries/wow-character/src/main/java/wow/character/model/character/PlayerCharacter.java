package wow.character.model.character;

import wow.character.model.build.Build;
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
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;

import java.util.List;
import java.util.Optional;

import static java.util.function.Function.identity;

/**
 * User: POlszewski
 * Date: 2023-10-31
 */
public interface PlayerCharacter extends Character {
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
	default boolean hasExclusiveFaction(ExclusiveFaction exclusiveFaction) {
		return getExclusiveFactions().has(exclusiveFaction);
	}

	Consumables getConsumables();

	@Override
	default Optional<Ability> getAbility(AbilityId abilityId) {
		var ability = Character.super.getAbility(abilityId);

		if (ability.isPresent()) {
			return ability;
		}

		ability = getEquipment().getAbility(abilityId).map(identity());

		if (ability.isPresent()) {
			return ability;
		}

		return getConsumables().getAbility(abilityId).map(identity());
	}
}
