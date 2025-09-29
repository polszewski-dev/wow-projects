package wow.commons.model.config;

import wow.commons.model.categorization.PveRole;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.PetType;
import wow.commons.model.character.RaceId;
import wow.commons.model.profession.ProfessionSpecializationId;
import wow.commons.model.pve.Side;
import wow.commons.model.spell.AbilityId;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
		String exclusiveFaction,
		List<PetType> activePet,
		AbilityId abilityId,
		TalentRestriction talentRestriction,
		PveRole role,
		Integer maxLevel
) {
	public static final CharacterRestriction EMPTY = new CharacterRestriction(null, List.of(), List.of(), null, null, null, null, List.of(), null, null, null, null);

	public CharacterRestriction {
		Objects.requireNonNull(characterClassIds);
		Objects.requireNonNull(raceIds);
		Objects.requireNonNull(activePet);
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
		if (!activePet.isEmpty() && activePet.stream().noneMatch(characterInfo::hasActivePet)) {
			return false;
		}
		if (abilityId != null && !characterInfo.hasAbility(abilityId)) {
			return false;
		}
		if (talentRestriction != null && !talentRestriction.isMetBy(characterInfo)) {
			return false;
		}
		return maxLevel == null || characterInfo.getLevel() <= maxLevel;
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
		if (!activePet.isEmpty()) {
			parts.add("pet: " + activePet);
		}
		if (abilityId != null) {
			parts.add("spell: " + abilityId);
		}
		if (talentRestriction != null) {
			parts.add("talentRestriction: " + talentRestriction);
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
