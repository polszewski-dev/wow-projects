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
	private final CharacterClass characterClass;
	private final Race race;
	private final int level;
	private final Phase phase;
	private final Build build;
	private final Equipment equipment;
	private final CharacterProfessions professions;
	private final Buffs buffs;
	private final BaseStatInfo baseStatInfo;
	private final CombatRatingInfo combatRatingInfo;

	private Enemy targetEnemy;

	public Character(CharacterClass characterClass, Race race, int level, Phase phase) {
		this.characterClass = characterClass;
		this.race = race;
		this.level = level;
		this.phase = phase;
		this.build = new Build();
		this.equipment = new Equipment();
		this.professions = new CharacterProfessions();
		this.buffs = new Buffs();
		this.baseStatInfo = characterClass.getBaseStatInfo(level, race.getRaceId());
		this.combatRatingInfo = characterClass.getGameVersion().getCombatRatingInfo(level);
	}

	@Override
	public Character copy() {
		return new Character(
				characterClass,
				race,
				level,
				phase,
				build.copy(),
				equipment.copy(),
				professions.copy(),
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

	public GameVersionId getGameVersionId() {
		return phase.getGameVersionId();
	}

	public GameVersion getGameVersion() {
		return phase.getGameVersion();
	}

	public PhaseId getPhaseId() {
		return phase.getPhaseId();
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

	// class

	@Override
	public CharacterClassId getCharacterClassId() {
		return characterClass.getCharacterClassId();
	}

	// race

	@Override
	public RaceId getRaceId() {
		return race.getRaceId();
	}

	@Override
	public Side getSide() {
		return race.getSide();
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

	public void setProfessions(List<CharacterProfession> professions) {
		this.professions.setProfessions(professions);
	}

	public void addProfession(ProfessionId professionId, ProfessionSpecializationId specializationId) {
		Profession profession = getGameVersion().getProfession(professionId);
		ProfessionSpecialization specialization = profession.getSpecialization(specializationId);

		professions.addProfession(profession, specialization);
	}

	public void addProfession(ProfessionId professionId) {
		addProfession(professionId, null);
	}

	public void resetProfessions() {
		professions.reset();
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
