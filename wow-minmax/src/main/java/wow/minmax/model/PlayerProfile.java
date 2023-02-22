package wow.minmax.model;

import lombok.Getter;
import lombok.Setter;
import wow.character.model.Copyable;
import wow.character.model.build.*;
import wow.character.model.character.Character;
import wow.character.model.character.*;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.PetType;
import wow.commons.model.character.Race;
import wow.commons.model.item.Item;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.TalentId;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * User: POlszewski
 * Date: 2021-11-04
 */
@Getter
@Setter
public class PlayerProfile implements AttributeCollection, Copyable<PlayerProfile> {
	private final UUID profileId;
	private final String profileName;
	private final Character character;
	private LocalDateTime lastModified;

	public PlayerProfile(UUID profileId, String profileName, Character character) {
		this.profileId = profileId;
		this.profileName = profileName;
		this.character = character;
		this.lastModified = LocalDateTime.now();
	}

	@Override
	public PlayerProfile copy() {
		return copy(profileId, profileName, character.getPhase());
	}

	public PlayerProfile copy(UUID profileId, String profileName, Phase phase) {
		PlayerProfile copy = new PlayerProfile(profileId, profileName, character.copy(phase));
		copy.lastModified = this.lastModified;
		return copy;
	}

	@Override
	public void collectAttributes(AttributeCollector collector) {
		character.collectAttributes(collector);
	}

	public PlayerProfileInfo getProfileInfo() {
		return new PlayerProfileInfo(
				profileId,
				profileName,
				character.getCharacterClass(),
				character.getRace(),
				character.getLevel(),
				character.getEnemyType(),
				character.getBuildId(),
				character.getProfessions(),
				character.getPhase(),
				lastModified
		);
	}

	// character

	public Attributes getStats() {
		return character.getStats();
	}

	public CharacterClass getCharacterClass() {
		return character.getCharacterClass();
	}

	public Race getRace() {
		return character.getRace();
	}

	public int getLevel() {
		return character.getLevel();
	}

	public CharacterProfessions getProfessions() {
		return character.getProfessions();
	}

	public Phase getPhase() {
		return character.getPhase();
	}

	public CombatRatingInfo getCombatRatingInfo() {
		return character.getCombatRatingInfo();
	}

	public Build getBuild() {
		return character.getBuild();
	}

	public Equipment getEquipment() {
		return character.getEquipment();
	}

	public Buffs getBuffs() {
		return character.getBuffs();
	}

	public BaseStatInfo getBaseStatInfo() {
		return character.getBaseStatInfo();
	}

	public Side getSide() {
		return character.getSide();
	}

	public GameVersion getGameVersion() {
		return character.getGameVersion();
	}

	public void equip(EquippableItem item, ItemSlot slot) {
		character.equip(item, slot);
	}

	public void equip(EquippableItem item) {
		character.equip(item);
	}

	public void setEquipment(Equipment equipment) {
		character.setEquipment(equipment);
	}

	public EquippableItem getEquippedItem(ItemSlot slot) {
		return character.getEquippedItem(slot);
	}

	public boolean canEquip(ItemSlot itemSlot, Item item) {
		return character.canEquip(itemSlot, item);
	}

	public void setBuffs(BuffSetId... buffSetIds) {
		character.setBuffs(buffSetIds);
	}

	public void resetBuild() {
		character.resetBuild();
	}

	public void resetEquipment() {
		character.resetEquipment();
	}

	public void resetBuffs() {
		character.resetBuffs();
	}

	public boolean hasProfession(Profession profession) {
		return character.hasProfession(profession);
	}

	public boolean hasProfessionSpecialization(ProfessionSpecialization specialization) {
		return character.hasProfessionSpecialization(specialization);
	}

	public BuildId getBuildId() {
		return character.getBuildId();
	}

	public String getTalentLink() {
		return character.getTalentLink();
	}

	public Talents getTalents() {
		return character.getTalents();
	}

	public PveRole getRole() {
		return character.getRole();
	}

	public Spell getDamagingSpell() {
		return character.getDamagingSpell();
	}

	public List<Spell> getRelevantSpells() {
		return character.getRelevantSpells();
	}

	public PetType getActivePet() {
		return character.getActivePet();
	}

	public BuffSets getBuffSets() {
		return character.getBuffSets();
	}

	public List<Buff> getBuffSet(BuffSetId buffSetId) {
		return character.getBuffSet(buffSetId);
	}

	public boolean hasTalent(TalentId talentId) {
		return character.hasTalent(talentId);
	}

	public void setBuffs(Collection<Buff> buffs) {
		character.setBuffs(buffs);
	}

	public void enableBuff(Buff buff, boolean enable) {
		character.enableBuff(buff, enable);
	}

	// enemy

	public CreatureType getEnemyType() {
		return character.getTargetEnemy().getEnemyType();
	}

	//

	@Override
	public String toString() {
		return profileName;
	}
}
