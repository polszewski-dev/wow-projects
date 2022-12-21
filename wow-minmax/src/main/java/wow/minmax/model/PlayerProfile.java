package wow.minmax.model;

import lombok.Getter;
import wow.character.model.Copyable;
import wow.character.model.build.BuffSetId;
import wow.character.model.build.Build;
import wow.character.model.build.BuildId;
import wow.character.model.build.PveRole;
import wow.character.model.character.Character;
import wow.character.model.character.CharacterProfession;
import wow.character.model.character.CharacterProfessions;
import wow.character.model.character.Enemy;
import wow.character.model.equipment.Equipment;
import wow.character.util.AttributeEvaluator;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.categorization.ItemSlot;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.character.CreatureType;
import wow.commons.model.character.PetType;
import wow.commons.model.character.Race;
import wow.commons.model.item.Item;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-11-04
 */
@Getter
public class PlayerProfile implements Copyable<PlayerProfile>, AttributeCollection {
	private final UUID profileId;
	private final String profileName;
	private final Character character;
	private final Enemy enemy;
	private Equipment equipment = new Equipment();
	private List<Buff> buffs = List.of();

	private LocalDateTime lastModified;

	public PlayerProfile(UUID profileId, String profileName, Character character, Enemy enemy) {
		this.profileId = profileId;
		this.profileName = profileName;
		this.character = character;
		this.enemy = enemy;
		this.lastModified = LocalDateTime.now();
	}

	@Override
	public PlayerProfile copy() {
		return copy(profileId, profileName, character.getPhase());
	}

	public PlayerProfile copy(UUID profileId, String profileName, Phase phase) {
		PlayerProfile copy = new PlayerProfile(profileId, profileName, character.setPhase(phase), enemy);
		copy.equipment = Copyable.copyNullable(this.equipment);
		copy.buffs = new ArrayList<>(this.buffs);
		copy.lastModified = this.lastModified;
		return copy;
	}

	@Override
	public <T extends AttributeCollector<T>> void collectAttributes(T collector) {
		equipment.collectAttributes(collector);
		Attributes talentAttributes = getTalentAttributes();
		collector.addAttributes(talentAttributes);
		collector.addAttributes(getBuffAttributesModifiedByTalents(talentAttributes));
	}

	private Attributes getTalentAttributes() {
		return AttributeEvaluator.of()
				.addAttributes(getTalents().values())
				.solveAllLeaveAbilities();
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
	}

	public void setBuffs(Collection<Buff> buffs) {
		validateExclusionGroups(buffs);
		this.buffs = buffs.stream()
				.distinct()
				.collect(Collectors.toList());
	}

	public void setBuffs(BuffSetId... buffSetIds) {
		Set<Buff> newBuffs = new HashSet<>();
		for (BuffSetId buffSetId : buffSetIds) {
			newBuffs.addAll(getBuild().getBuffSet(buffSetId));
		}
		setBuffs(newBuffs);
	}

	public void enableBuff(Buff buff, boolean enable) {
		if (!enable) {
			buffs.removeIf(existingBuff -> Objects.equals(existingBuff.getId(), buff.getId()));
			return;
		}

		if (hasBuff(buff)) {
			return;
		}

		if (buff.getExclusionGroup() != null) {
			buffs.removeIf(existingBuff -> existingBuff.getExclusionGroup() == buff.getExclusionGroup());
		}

		buffs.add(buff);
	}

	private boolean hasBuff(Buff buff) {
		return buffs.stream().anyMatch(existingBuff -> Objects.equals(existingBuff.getId(), buff.getId()));
	}

	private void validateExclusionGroups(Collection<Buff> buffs) {
		var groups = buffs.stream()
				.filter(buff -> buff.getExclusionGroup() != null)
				.collect(Collectors.groupingBy(Buff::getExclusionGroup));

		for (var group : groups.entrySet()) {
			if (group.getValue().size() > 1) {
				throw new IllegalArgumentException("Group:  " + group.getKey() + " has more than one buff");
			}
		}
	}

	private List<AttributeSource> getBuffAttributesModifiedByTalents(Attributes talentAttributes) {
		List<AttributeSource> result = new ArrayList<>(buffs.size());

		for (Buff buff : buffs) {
			if (buff.getType() == BuffType.SELF_BUFF) {
				Percent effectIncreasePct = talentAttributes.getEffectIncreasePct(buff.getSourceSpell());
				result.add(buff.modifyEffectByPct(effectIncreasePct));
			} else {
				result.add(buff);
			}
		}

		return result;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public Attributes getStats() {
		return AttributeEvaluator.of()
				.addAttributes(this)
				.solveAllLeaveAbilities();
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

	public Build getBuild() {
		return character.getBuild();
	}

	public List<CharacterProfession> getProfessions() {
		return character.getProfessions();
	}

	public CharacterProfessions getCharacterProfessions() {
		return character.getCharacterProfessions();
	}

	public Phase getPhase() {
		return character.getPhase();
	}

	public Side getSide() {
		return character.getSide();
	}

	public boolean hasProfession(Profession profession) {
		return character.hasProfession(profession);
	}

	public boolean hasProfession(Profession profession, int level) {
		return character.hasProfession(profession, level);
	}

	public boolean hasProfessionSpecialization(ProfessionSpecialization specialization) {
		return character.hasProfessionSpecialization(specialization);
	}

	public boolean hasTalent(TalentId talentId) {
		return character.hasTalent(talentId);
	}

	public CreatureType getEnemyType() {
		return enemy.getEnemyType();
	}

	public BuildId getBuildId() {
		return getBuild().getBuildId();
	}

	public String getTalentLink() {
		return getBuild().getTalentLink();
	}

	public Map<TalentId, Talent> getTalents() {
		return getBuild().getTalents();
	}

	public PveRole getRole() {
		return getBuild().getRole();
	}

	public Spell getDamagingSpell() {
		return getBuild().getDamagingSpell();
	}

	public List<Spell> getRelevantSpells() {
		return getBuild().getRelevantSpells();
	}

	public PetType getActivePet() {
		return getBuild().getActivePet();
	}

	public Map<BuffSetId, List<Buff>> getBuffSets() {
		return getBuild().getBuffSets();
	}

	public boolean canEquip(ItemSlot itemSlot, Item item) {
		return character.canEquip(itemSlot, item.getItemType(), item.getItemSubType());
	}

	@Override
	public String toString() {
		return profileName;
	}
}
