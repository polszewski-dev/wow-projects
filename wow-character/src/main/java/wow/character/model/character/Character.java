package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.Copyable;
import wow.character.model.build.*;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.util.AttributeEvaluator;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.PetType;
import wow.commons.model.character.Race;
import wow.commons.model.config.CharacterInfo;
import wow.commons.model.item.Item;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.TalentId;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@AllArgsConstructor
@Getter
public class Character implements AttributeCollection, CharacterInfo, Copyable<Character> {
	private final CharacterClass characterClass;
	private final Race race;
	private final int level;
	private final CharacterProfessions professions;
	private final Phase phase;
	private final Build build;
	private final Equipment equipment;
	private final Buffs buffs;
	private final BaseStatInfo baseStatInfo;
	private final CombatRatingInfo combatRatingInfo;

	private Enemy targetEnemy;

	public Character(CharacterClass characterClass, Race race, int level, CharacterProfessions professions, Phase phase, BaseStatInfo baseStatInfo, CombatRatingInfo combatRatingInfo) {
		this.characterClass = characterClass;
		this.race = race;
		this.level = level;
		this.professions = professions;
		this.phase = phase;
		this.build = new Build();
		this.equipment = new Equipment();
		this.buffs = new Buffs();
		this.baseStatInfo = baseStatInfo;
		this.combatRatingInfo = combatRatingInfo;
	}

	@Override
	public Character copy() {
		return copy(phase);
	}

	public Character copy(Phase phase) {
		return new Character(
				characterClass,
				race,
				level,
				professions,
				phase,
				build.copy(),
				equipment.copy(),
				buffs.copy(),
				baseStatInfo,
				combatRatingInfo,
				targetEnemy.copy()
		);
	}

	@Override
	public <T extends AttributeCollector<T>> void collectAttributes(T collector) {
		build.collectAttributes(collector);
		equipment.collectAttributes(collector);
		buffs.collectAttributes(collector);
		targetEnemy.collectAttributes(collector);
	}

	public Side getSide() {
		return race.getSide();
	}

	public GameVersion getGameVersion() {
		return phase.getGameVersion();
	}

	public void equip(EquippableItem item, ItemSlot slot) {
		equipment.equip(item, slot);
	}

	public void equip(EquippableItem item) {
		equipment.equip(item);
	}

	public void setEquipment(Equipment equipment) {
		this.equipment.setEquipment(equipment);
	}

	public EquippableItem getEquippedItem(ItemSlot slot) {
		return equipment.get(slot);
	}

	public boolean canEquip(ItemSlot itemSlot, Item item) {
		return characterClass.canEquip(itemSlot, item.getItemType(), item.getItemSubType());
	}

	public void setBuffs(BuffSetId... buffSetIds) {
		Set<Buff> newBuffs = new HashSet<>();
		for (BuffSetId buffSetId : buffSetIds) {
			newBuffs.addAll(build.getBuffSet(buffSetId));
		}
		buffs.setBuffs(newBuffs);
	}

	public void resetBuild() {
		build.reset();
	}

	public void resetEquipment() {
		equipment.reset();
	}

	public void resetBuffs() {
		buffs.reset();
	}

	public void setTargetEnemy(Enemy targetEnemy) {
		this.targetEnemy = targetEnemy;
	}

	public Attributes getStats() {
		return AttributeEvaluator.of()
				.addAttributes(this)
				.solveAllLeaveAbilities();
	}

	// professions

	@Override
	public boolean hasProfession(Profession profession) {
		return professions.hasProfession(profession);
	}

	@Override
	public boolean hasProfessionSpecialization(ProfessionSpecialization specialization) {
		return professions.hasProfessionSpecialization(specialization);
	}

	// build

	public BuildId getBuildId() {
		return build.getBuildId();
	}

	public String getTalentLink() {
		return build.getTalentLink();
	}

	public Talents getTalents() {
		return build.getTalents();
	}

	public PveRole getRole() {
		return build.getRole();
	}

	public Spell getDamagingSpell() {
		return build.getDamagingSpell();
	}

	public List<Spell> getRelevantSpells() {
		return build.getRelevantSpells();
	}

	public PetType getActivePet() {
		return build.getActivePet();
	}

	public BuffSets getBuffSets() {
		return build.getBuffSets();
	}

	public List<Buff> getBuffSet(BuffSetId buffSetId) {
		return build.getBuffSet(buffSetId);
	}

	@Override
	public boolean hasTalent(TalentId talentId) {
		return build.hasTalent(talentId);
	}

	// buffs

	public void setBuffs(Collection<Buff> buffs) {
		this.buffs.setBuffs(buffs);
	}

	public void enableBuff(Buff buff, boolean enable) {
		buffs.enableBuff(buff, enable);
	}

	// enemy

	public CreatureType getEnemyType() {
		return targetEnemy.getEnemyType();
	}
}
