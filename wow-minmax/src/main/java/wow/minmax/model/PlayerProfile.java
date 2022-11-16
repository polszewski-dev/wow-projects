package wow.minmax.model;

import wow.commons.model.Copyable;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.model.unit.*;
import wow.commons.util.AttributesBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-11-04
 */

public class PlayerProfile implements Copyable<PlayerProfile>, AttributeCollection {
	private final UUID profileId;
	private final String profileName;
	private final CharacterInfo characterInfo;
	private final CreatureType enemyType;
	private final Phase phase;
	private Build build;
	private Equipment equipment;
	private List<Buff> buffs;

	private LocalDateTime lastModified;

	public PlayerProfile(UUID profileId, String profileName, CharacterInfo characterInfo, CreatureType enemyType, Phase phase, Build build) {
		this.profileId = profileId;
		this.profileName = profileName;
		this.characterInfo = characterInfo;
		this.enemyType = enemyType;
		this.phase = phase;
		this.build = build;
		this.lastModified = LocalDateTime.now();
	}

	@Override
	public PlayerProfile copy() {
		return copy(profileId, profileName, phase);
	}

	public PlayerProfile copy(UUID profileId, String profileName, Phase phase) {
		PlayerProfile copy = new PlayerProfile(profileId, profileName, characterInfo, enemyType, phase, build);

		copy.equipment = Copyable.copyNullable(this.equipment);
		copy.buffs = new ArrayList<>(this.buffs);
		copy.lastModified = this.lastModified;

		return copy;
	}

	public LocalDateTime getLastModified() {
		return lastModified;
	}

	public void setLastModified(LocalDateTime lastModified) {
		this.lastModified = lastModified;
	}

	public UUID getProfileId() {
		return profileId;
	}

	public String getProfileName() {
		return profileName;
	}

	public CharacterInfo getCharacterInfo() {
		return characterInfo;
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

	public PetType getActivePet() {
		return build.getActivePet();
	}

	public CreatureType getEnemyType() {
		return enemyType;
	}

	public Phase getPhase() {
		return phase;
	}

	public Build getBuild() {
		return build;
	}

	public Spell getDamagingSpell() {
		return build.getDamagingSpell();
	}

	public SpellId getDamagingSpellId() {
		return build.getDamagingSpell().getSpellId();
	}

	public Duration getDamagingSpellCastTime() {
		return build.getDamagingSpell().getCastTime();
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		this.equipment = equipment;
		this.lastModified = LocalDateTime.now();
	}

	public List<Buff> getBuffs() {
		return buffs;
	}

	public void setBuffs(List<Buff> buffs) {
		validateExclusionGroups(buffs);
		this.buffs = buffs;
		this.lastModified = LocalDateTime.now();
	}

	private void validateExclusionGroups(List<Buff> buffs) {
		var groups = buffs.stream()
						  .filter(buff -> buff.getExclusionGroup() != null)
						  .collect(Collectors.groupingBy(Buff::getExclusionGroup));

		for (var group : groups.entrySet()) {
			if (group.getValue().size() > 1) {
				throw new IllegalArgumentException("Group:  " + group.getKey() + " has more than one buff");
			}
		}
	}

	public List<TalentInfo> getTalentInfos() {
		return build.getTalentInfos();
	}

	public void setBuild(Build build) {
		this.build = build;
		this.lastModified = LocalDateTime.now();
	}

	public void enableBuff(Buff buff, boolean enable) {
		if (!enable) {
			buffs.removeIf(existingBuff -> existingBuff.getId() == buff.getId());
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
		return buffs.stream().anyMatch(existingBuff -> existingBuff.getId() == buff.getId());
	}

	public PVERole getRole() {
		return build.getRole();
	}

	@Override
	public <T extends AttributeCollector<T>> void collectAttributes(T collector) {
		collector.addAttributes(getBuffsModifiedByTalents());
		collector.addAttributes(build.getTalentInfos());
		equipment.collectAttributes(collector);
	}

	private List<AttributeSource> getBuffsModifiedByTalents() {
		Attributes talentAttributes = getTalentAttributes();
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

	private Attributes getTalentAttributes() {
		return new AttributesBuilder()
				.addAttributes(build.getTalentInfos())
				.toAttributes();
	}

	@Override
	public String toString() {
		return profileName;
	}
}
