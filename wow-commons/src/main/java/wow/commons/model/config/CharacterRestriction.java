package wow.commons.model.config;

import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.ExclusiveFaction;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.professions.ProfessionSpecializationId;
import wow.commons.model.pve.Side;
import wow.commons.model.spells.SpellId;
import wow.commons.model.talents.TalentId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static wow.commons.util.CollectionUtil.mergeCriteria;
import static wow.commons.util.CollectionUtil.mergeValues;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
public record CharacterRestriction(
		Integer level,
		List<CharacterClassId> characterClassIds,
		List<RaceId> raceIds,
		Side side,
		ProfessionRestriction professionRestriction,
		ProfessionSpecializationId professionSpecId,
		ExclusiveFaction exclusiveFaction,
		PetType activePet,
		SpellId spellId,
		TalentId talentId,
		PveRole role,
		Integer maxLevel
) {
	public static final CharacterRestriction EMPTY = new CharacterRestriction(null, List.of(), List.of(), null, null, null, null, null, null, null, null, null);

	public CharacterRestriction {
		Objects.requireNonNull(characterClassIds);
		Objects.requireNonNull(raceIds);
	}

	public boolean isMetBy(CharacterInfo characterInfo) {
		if (this.level != null && characterInfo.getLevel() < this.level) {
			return false;
		}
		if (!characterClassIds.isEmpty() && !characterClassIds.contains(characterInfo.getCharacterClassId())) {
			return false;
		}
		if (!this.raceIds.isEmpty() && !this.raceIds.contains(characterInfo.getRaceId())) {
			return false;
		}
		if (side != null && side != characterInfo.getSide()) {
			return false;
		}
		if (professionRestriction != null && !characterInfo.hasProfession(professionRestriction.professionId(), professionRestriction.level())) {
			return false;
		}
		if (professionSpecId != null && !characterInfo.hasProfessionSpecialization(professionSpecId)) {
			return false;
		}
		if (exclusiveFaction != null && !characterInfo.hasExclusiveFaction(exclusiveFaction)) {
			return false;
		}
		if (activePet != null && !characterInfo.hasActivePet(activePet)) {
			return false;
		}
		if (spellId != null && !characterInfo.hasSpell(spellId)) {
			return false;
		}
		if (talentId != null && !characterInfo.hasTalent(talentId)) {
			return false;
		}
		return maxLevel == null || characterInfo.getLevel() <= maxLevel;
	}

	public CharacterRestriction merge(CharacterRestriction other) {
		return new CharacterRestriction(
				mergeValues(level, other.level),
				mergeCriteria(characterClassIds, other.characterClassIds),
				mergeCriteria(raceIds, other.raceIds),
				mergeValues(side, other.side),
				mergeValues(professionRestriction, other.professionRestriction),
				mergeValues(professionSpecId, other.professionSpecId),
				mergeValues(exclusiveFaction, other.exclusiveFaction),
				mergeValues(activePet, other.activePet),
				mergeValues(spellId, other.spellId),
				mergeValues(talentId, other.talentId),
				mergeValues(role, other.role),
				mergeValues(maxLevel, other.maxLevel)
		);
	}

	@Override
	public String toString() {
		List<String> parts = new ArrayList<>();
		if (level != null) {
			parts.add("level: " + level);
		}
		if (!characterClassIds.isEmpty()) {
			parts.add("characterClasses: " + characterClassIds);
		}
		if (!raceIds.isEmpty()) {
			parts.add("races: " + raceIds);
		}
		if (side != null) {
			parts.add("side: " + side);
		}
		if (professionRestriction != null) {
			parts.add("profession: " + professionRestriction);
		}
		if (professionSpecId != null) {
			parts.add("professionSpec: " + professionSpecId);
		}
		if (exclusiveFaction != null) {
			parts.add("xfaction: " + exclusiveFaction);
		}
		if (activePet != null) {
			parts.add("pet: " + activePet);
		}
		if (spellId != null) {
			parts.add("spell: " + spellId);
		}
		if (talentId != null) {
			parts.add("talentId: " + talentId);
		}
		if (role != null) {
			parts.add("role: " + role);
		}
		if (maxLevel != null) {
			parts.add("maxLvl: " + maxLevel);
		}
		return parts.stream().collect(Collectors.joining(", ", "(", ")"));
	}
}
