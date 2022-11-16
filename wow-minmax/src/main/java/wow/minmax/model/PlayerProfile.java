package wow.minmax.model;

import wow.commons.model.Copyable;
import wow.commons.model.Duration;
import wow.commons.model.attributes.AttributeCollection;
import wow.commons.model.attributes.AttributeCollector;
import wow.commons.model.buffs.Buff;
import wow.commons.model.equipment.Equipment;
import wow.commons.model.pve.Phase;
import wow.commons.model.spells.Spell;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.TalentInfo;
import wow.commons.model.unit.*;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

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

	private LocalDateTime lastModified;

	public PlayerProfile(UUID profileId, String profileName, CharacterInfo characterInfo, CreatureType enemyType, Phase phase) {
		this.profileId = profileId;
		this.profileName = profileName;
		this.characterInfo = characterInfo;
		this.enemyType = enemyType;
		this.phase = phase;
		this.lastModified = LocalDateTime.now();
	}

	@Override
	public PlayerProfile copy() {
		return copy(profileId, profileName, phase);
	}

	public PlayerProfile copy(UUID profileId, String profileName, Phase phase) {
		PlayerProfile copy = new PlayerProfile(profileId, profileName, characterInfo, enemyType, phase);
		copy.build = Copyable.copyNullable(this.build);
		copy.equipment = Copyable.copyNullable(this.equipment);
		copy.lastModified = this.lastModified;
		return copy;
	}

	@Override
	public <T extends AttributeCollector<T>> void collectAttributes(T collector) {
		build.collectAttributes(collector);
		equipment.collectAttributes(collector);
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
	}

	public List<Buff> getBuffs() {
		return build.getBuffs();
	}

	public void setBuffs(List<Buff> buffs) {
		build.setBuffs(buffs);
	}

	public void setBuffs(Build.BuffSetId... buffSetIds) {
		build.setBuffsFromSets(buffSetIds);
	}

	public Collection<TalentInfo> getTalentInfos() {
		return build.getTalentInfos().values();
	}

	public void setBuild(Build build) {
		this.build = build;
	}

	public void enableBuff(Buff buff, boolean enable) {
		build.enableBuff(buff, enable);
	}

	public PVERole getRole() {
		return build.getRole();
	}

	@Override
	public String toString() {
		return profileName;
	}
}
