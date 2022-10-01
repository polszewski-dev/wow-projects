package wow.minmax.model;

import wow.commons.model.Copyable;
import wow.commons.model.Percent;
import wow.commons.model.attributes.*;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.model.unit.CharacterClass;
import wow.commons.model.unit.CreatureType;
import wow.commons.model.unit.Race;
import wow.commons.util.AttributesBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-11-04
 */

public class PlayerProfile implements Copyable<PlayerProfile>, AttributeCollection {
	private final UUID profileId;
	private String profileName;

	private final CharacterClass characterClass;
	private final Race race;
	private final int level;
	private final CreatureType enemyType;

	private final int phase;

	private Build build;

	private Equipment equipment;
	private List<Buff> buffs;

	private final boolean readOnly;
	private LocalDateTime lastModified;

	public PlayerProfile(UUID profileId, String profileName, CharacterClass characterClass, Race race, int level, CreatureType enemyType, int phase, Build build) {
		this(profileId, profileName, characterClass, race, level, enemyType, phase, build, false);
	}

	private PlayerProfile(UUID profileId, String profileName, CharacterClass characterClass, Race race, int level, CreatureType enemyType, int phase, Build build, boolean readOnly) {
		this.profileId = profileId;
		this.profileName = profileName;
		this.characterClass = characterClass;
		this.race = race;
		this.level = level;
		this.enemyType = enemyType;
		this.phase = phase;
		this.build = build;
		this.readOnly = readOnly;
		this.lastModified = LocalDateTime.now();
	}

	@Override
	public PlayerProfile copy(boolean readOnly) {
		if (this.readOnly && readOnly) {
			return this;
		}

		return copy(profileId, profileName, phase, readOnly);
	}

	public PlayerProfile copy(UUID profileId, String profileName, int phase) {
		return copy(profileId, profileName, phase, false);
	}

	public PlayerProfile readOnlyCopy(UUID profileId, String profileName, int phase) {
		return copy(profileId, profileName, phase, true);
	}

	private PlayerProfile copy(UUID profileId, String profileName, int phase, boolean readOnly) {
		PlayerProfile copy = new PlayerProfile(profileId, profileName, characterClass, race, level, enemyType, phase, build, readOnly);

		copy.equipment = Copyable.copyNullable(this.equipment, readOnly);
		copy.buffs = new ArrayList<>(this.buffs);

		if (readOnly) {
			copy.buffs = Collections.unmodifiableList(copy.buffs);
		}

		copy.lastModified = this.lastModified;

		return copy;
	}

	@Override
	public boolean isReadOnly() {
		return readOnly;
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

	public CharacterClass getCharacterClass() {
		return characterClass;
	}

	public Race getRace() {
		return race;
	}

	public int getLevel() {
		return level;
	}

	public CreatureType getEnemyType() {
		return enemyType;
	}

	public int getPhase() {
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

	public Equipment getEquipment() {
		return equipment;
	}

	public void setEquipment(Equipment equipment) {
		assertCanBeModified();
		this.equipment = equipment;
		this.lastModified = LocalDateTime.now();
	}

	public List<Buff> getBuffs() {
		return buffs;
	}

	public void setBuffs(List<Buff> buffs) {
		assertCanBeModified();
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
		assertCanBeModified();
		this.build = build;
		this.lastModified = LocalDateTime.now();
	}

	@Override
	public <T extends AttributeCollector> T collectAttributes(T collector) {
		collector.addAttributes(getBuffsModifiedByTalents());
		collector.addAttributes(build.getTalentInfos());
		equipment.collectAttributes(collector);
		return collector;
	}

	private List<AttributeSource> getBuffsModifiedByTalents() {
		List<AttributeSource> result = new ArrayList<>(buffs.size());

		for (Buff buff : buffs) {
			if (buff.getType() == BuffType.SELF_BUFF) {
				Percent effectIncreasePct = getEffectIncreasePct(buff.getSourceSpell());

				if (!effectIncreasePct.isZero()) {
					result.add(buff.modifyEffectByPct(effectIncreasePct));
				} else {
					result.add(buff);
				}
			} else {
				result.add(buff);
			}
		}

		return result;
	}

	private Percent getEffectIncreasePct(SpellId sourceSpell) {
		if (sourceSpell == null) {
			return Percent.ZERO;
		}

		Attributes selfBuffModifiers = new AttributesBuilder(AttributeCondition.of(sourceSpell))
				.addAttributes(build.getTalentInfos())
				.toAttributes();

		return selfBuffModifiers.getEffectIncreasePct();
	}

	@Override
	public String toString() {
		return profileName;
	}
}
