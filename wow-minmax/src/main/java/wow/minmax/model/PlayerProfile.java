package wow.minmax.model;

import lombok.Getter;
import wow.commons.model.Copyable;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.character.*;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.professions.Profession;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.spells.Spell;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentId;
import wow.commons.util.AttributeEvaluator;

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
	private final CharacterInfo characterInfo;
	private final CreatureType enemyType;
	private Equipment equipment = new Equipment();
	private List<Buff> buffs = List.of();

	private LocalDateTime lastModified;

	public PlayerProfile(UUID profileId, String profileName, CharacterInfo characterInfo, CreatureType enemyType) {
		this.profileId = profileId;
		this.profileName = profileName;
		this.characterInfo = characterInfo;
		this.enemyType = enemyType;
		this.lastModified = LocalDateTime.now();
	}

	@Override
	public PlayerProfile copy() {
		return copy(profileId, profileName, characterInfo.getPhase());
	}

	public PlayerProfile copy(UUID profileId, String profileName, Phase phase) {
		PlayerProfile copy = new PlayerProfile(profileId, profileName, characterInfo.setPhase(phase), enemyType);
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
		return characterInfo.getCharacterClass();
	}

	public Race getRace() {
		return characterInfo.getRace();
	}

	public int getLevel() {
		return characterInfo.getLevel();
	}

	public Build getBuild() {
		return characterInfo.getBuild();
	}

	public List<CharacterProfession> getProfessions() {
		return characterInfo.getProfessions();
	}

	public CharacterProfessions getCharacterProfessions() {
		return characterInfo.getCharacterProfessions();
	}

	public Phase getPhase() {
		return characterInfo.getPhase();
	}

	public Side getSide() {
		return characterInfo.getSide();
	}

	public boolean hasProfession(Profession profession) {
		return characterInfo.hasProfession(profession);
	}

	public boolean hasProfession(Profession profession, int level) {
		return characterInfo.hasProfession(profession, level);
	}

	public boolean hasProfessionSpecialization(ProfessionSpecialization specialization) {
		return characterInfo.hasProfessionSpecialization(specialization);
	}

	public boolean hasTalent(TalentId talentId) {
		return characterInfo.hasTalent(talentId);
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

	@Override
	public String toString() {
		return profileName;
	}
}
