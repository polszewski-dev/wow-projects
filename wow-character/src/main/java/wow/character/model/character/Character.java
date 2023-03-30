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
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.config.CharacterInfo;
import wow.commons.model.item.Item;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
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
	private final CharacterClassId characterClassId;
	private final RaceId raceId;
	private final int level;
	private final CharacterProfessions professions;
	private final PhaseId phaseId;
	private final Build build;
	private final Equipment equipment;
	private final Buffs buffs;
	private final BaseStatInfo baseStatInfo;
	private final CombatRatingInfo combatRatingInfo;

	private Enemy targetEnemy;

	public Character(CharacterClassId characterClassId, RaceId raceId, int level, CharacterProfessions professions, PhaseId phaseId, BaseStatInfo baseStatInfo, CombatRatingInfo combatRatingInfo) {
		this.characterClassId = characterClassId;
		this.raceId = raceId;
		this.level = level;
		this.professions = professions;
		this.phaseId = phaseId;
		this.build = new Build();
		this.equipment = new Equipment();
		this.buffs = new Buffs();
		this.baseStatInfo = baseStatInfo;
		this.combatRatingInfo = combatRatingInfo;
	}

	@Override
	public Character copy() {
		return copy(phaseId);
	}

	public Character copy(PhaseId phaseId) {
		return new Character(
				characterClassId,
				raceId,
				level,
				professions,
				phaseId,
				build.copy(),
				equipment.copy(),
				buffs.copy(),
				baseStatInfo,
				combatRatingInfo,
				targetEnemy.copy()
		);
	}

	@Override
	public void collectAttributes(AttributeCollector collector) {
		build.collectAttributes(collector);
		equipment.collectAttributes(collector);
		buffs.collectAttributes(collector);
		targetEnemy.collectAttributes(collector);
	}

	public Side getSide() {
		return raceId.getSide();
	}

	public GameVersionId getGameVersionId() {
		return phaseId.getGameVersionId();
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
		return characterClassId.canEquip(itemSlot, item.getItemType(), item.getItemSubType());
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
	public boolean hasProfession(ProfessionId professionId) {
		return professions.hasProfession(professionId);
	}

	@Override
	public boolean hasProfessionSpecialization(ProfessionSpecializationId specializationId) {
		return professions.hasProfessionSpecialization(specializationId);
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

	public Set<AttributeCondition> getConditions(Spell spell) {
		var result = new HashSet<AttributeCondition>();

		result.addAll(spell.getConditions());
		result.addAll(professions.getConditions());
		result.addAll(targetEnemy.getConditions());
		result.add(AttributeCondition.of(getActivePet()));
		result.add(AttributeCondition.EMPTY);

		return result;
	}
}
