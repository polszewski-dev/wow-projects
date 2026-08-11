package wow.character.model.character;

import wow.character.model.effect.EffectCollection;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.model.talent.Talents;
import wow.commons.model.Percent;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.*;
import wow.commons.model.config.CharacterInfo;
import wow.commons.model.effect.RacialEffect;
import wow.commons.model.item.Item;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.Ability;
import wow.commons.model.spell.AbilityId;
import wow.commons.model.spell.ResourceType;
import wow.commons.model.talent.TalentTree;

import java.util.List;
import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public interface Character extends CharacterInfo, EffectCollection {
	String getName();

	Phase getPhase();

	default PhaseId getPhaseId() {
		return getPhase().getPhaseId();
	}

	default GameVersion getGameVersion() {
		return getPhase().getGameVersion();
	}

	default GameVersionId getGameVersionId() {
		return getPhase().getGameVersionId();
	}

	CharacterClass getCharacterClass();

	@Override
	default CharacterClassId getCharacterClassId() {
		return getCharacterClass().getCharacterClassId();
	}

	CreatureType getCreatureType();

	// race

	Race getRace();

	@Override
	default RaceId getRaceId() {
		var race = getRace();

		return race != null ? race.getRaceId() : null;
	}

	default List<RacialEffect> getRacials() {
		var race = getRace();

		return race != null ? race.getRacials(this) : List.of();
	}

	// target

	Character getTarget();

	void setTarget(Character target);

	// stats

	BaseStatInfo getBaseStatInfo();

	CombatRatingInfo getCombatRatingInfo();

	default double getBaseStatValue(ResourceType resourceType) {
		return switch (resourceType) {
			case HEALTH -> getBaseStatInfo().getBaseHealth();
			case MANA -> getBaseStatInfo().getBaseMana();
			default -> throw new IllegalArgumentException("Unhandled resource: " + resourceType);
		};
	}

	// build

	Talents getTalents();

	@Override
	default boolean hasTalent(String name) {
		return getTalents().has(name);
	}

	default String getTalentLink() {
		return getTalents().getTalentLink();
	}

	PveRole getRole();

	void setRole(PveRole role);

	String getScript();

	void setScript(String script);

	void resetBuild();

	void invalidateCaches();

	// spellbook

	Spellbook getSpellbook();

	Optional<Ability> getAbility(AbilityId abilityId);

	default Optional<Ability> getAbility(AbilityId abilityId, int rank) {
		return getSpellbook().getAbility(abilityId, rank);
	}

	default Optional<Ability> getAbility(String abilityName) {
		return getSpellbook().getAbility(abilityName);
	}

	default Optional<Ability> getAbility(String abilityName, int rank) {
		return getSpellbook().getAbility(abilityName, rank);
	}

	// equipment

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

	default void resetEquipment() {
		getEquipment().reset();
	}

	// consumables

	Consumables getConsumables();

	// buffs

	Buffs getBuffs();

	default void resetBuffs() {
		getBuffs().reset();
	}

	// pet

	PetCharacter getActivePet();

	void setActivePet(PetCharacter pet);

	default PetType getActivePetType() {
		return getActivePet() != null ? getActivePet().getPetType() : null;
	}

	@Override
	default PetType getPetType() {
		return null;
	}

	default boolean isPet() {
		return getPetType() != null;
	}

	//

	static int getLevelDifference(Character caster, Character target) {
		return target.getLevel() - caster.getLevel();
	}

	Percent getHealthPct();

	void setHealthPct(Percent healthPct);

	default MovementType getMovementType() {
		return MovementType.RUNNING;
	}

	default FormType getForm() {
		return FormType.CASTER_FORM;
	}

	default boolean hasEffect(String effectName) {
		return false;
	}

	default int getNumberOfEffects(TalentTree tree) {
		return 0;
	}

	default boolean isFriendlyWith(Character target) {
		return this.getSide().isFriendlyWith(target.getSide());
	}

	default boolean isHostileWith(Character target) {
		return !isFriendlyWith(target);
	}
}
