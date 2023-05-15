package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.Copyable;
import wow.character.model.build.Build;
import wow.character.model.build.Rotation;
import wow.character.model.build.Talents;
import wow.character.model.equipment.Equipment;
import wow.character.model.equipment.EquippableItem;
import wow.character.util.AttributeEvaluator;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.buffs.Buff;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.*;
import wow.commons.model.config.CharacterInfo;
import wow.commons.model.item.Item;
import wow.commons.model.professions.ProfessionId;
import wow.commons.model.professions.ProfessionSpecializationId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.pve.Side;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.TalentId;

import java.util.*;

import static wow.character.model.character.BuffListType.CHARACTER_BUFF;

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
	private final Spellbook spellbook;
	private final Equipment equipment;
	private final CharacterProfessions professions;
	private final ExclusiveFactions exclusiveFactions;
	private final Buffs buffs;
	private final BaseStatInfo baseStatInfo;
	private final CombatRatingInfo combatRatingInfo;

	private Enemy targetEnemy;

	private final Map<SpellId, Set<AttributeCondition>> conditionCache;

	public Character(CharacterClass characterClass, Race race, int level, Phase phase, Talents talents) {
		this.characterClass = characterClass;
		this.race = race;
		this.level = level;
		this.phase = phase;
		this.build = new Build(phase.getGameVersion(), talents);
		this.spellbook = new Spellbook();
		this.equipment = new Equipment();
		this.professions = new CharacterProfessions();
		this.exclusiveFactions = new ExclusiveFactions();
		this.buffs = new Buffs(CHARACTER_BUFF);
		this.baseStatInfo = characterClass.getBaseStatInfo(level, race.getRaceId());
		this.combatRatingInfo = characterClass.getGameVersion().getCombatRatingInfo(level);
		this.conditionCache = new HashMap<>();
	}

	@Override
	public Character copy() {
		return new Character(
				characterClass,
				race,
				level,
				phase,
				build.copy(),
				spellbook.copy(),
				equipment.copy(),
				professions.copy(),
				exclusiveFactions.copy(),
				buffs.copy(),
				baseStatInfo,
				combatRatingInfo,
				targetEnemy.copy(),
				new HashMap<>(conditionCache)
		);
	}

	@Override
	public void collectAttributes(AttributeCollector collector) {
		build.collectAttributes(collector);
		equipment.collectAttributes(collector);
		buffs.collectAttributes(collector);
		race.collectAttributes(collector, this);
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

	public List<Racial> getRacials() {
		return race.getRacials(this);
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

	@Override
	public boolean hasActivePet(PetType petType) {
		return getActivePet().getPetType() == petType;
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

	@Override
	public boolean hasTalent(TalentId talentId) {
		return build.hasTalent(talentId);
	}

	// build

	public Talents getTalents() {
		return build.getTalents();
	}

	@Override
	public PveRole getRole() {
		return build.getRole();
	}

	public Rotation getRotation() {
		return build.getRotation();
	}

	public Pet getActivePet() {
		return build.getActivePet();
	}

	@Override
	public boolean hasExclusiveFaction(ExclusiveFaction exclusiveFaction) {
		return exclusiveFactions.has(exclusiveFaction);
	}

	@Override
	public boolean hasSpell(SpellId spellId) {
		return spellbook.getSpell(spellId).isPresent();
	}

	// buffs

	public Buffs getBuffList(BuffListType buffListType) {
		return switch (buffListType) {
			case CHARACTER_BUFF -> buffs;
			case TARGET_DEBUFF -> targetEnemy.getDebuffs();
		};
	}

	public void setBuffs(Collection<Buff> buffs) {
		this.buffs.set(buffs);
	}

	public void enableBuff(Buff buff, boolean enable) {
		buffs.enable(buff, enable);
	}

	// enemy

	public CreatureType getEnemyType() {
		return targetEnemy.getEnemyType();
	}

	public Set<AttributeCondition> getConditions(Spell spell) {
		SpellId spellId = spell != null ? spell.getSpellId() : null;

		return conditionCache.computeIfAbsent(spellId, x -> newConditions(spell));
	}

	private Set<AttributeCondition> newConditions(Spell spell) {
		var result = new HashSet<AttributeCondition>();

		result.addAll(professions.getConditions());
		result.addAll(targetEnemy.getConditions());
		result.addAll(getActivePet().getConditions());
		result.add(AttributeCondition.EMPTY);

		if (spell != null) {
			result.addAll(spell.getConditions());
		}

		return result;
	}
}
