package wow.commons.model.config;

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
		TalentId talentId
) {
	public static final CharacterRestriction EMPTY = new CharacterRestriction(null, List.of(), List.of(), null, null, null, null, null, null, null);

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
		if (professionRestriction != null && !characterInfo.hasProfession(professionRestriction.professionId())) {
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
		return talentId == null || characterInfo.hasTalent(talentId);
	}

	public CharacterRestriction merge(CharacterRestriction other) {
		return new CharacterRestriction(
				mergeValues(level, other.level),
				mergeCriteria(characterClassIds, other.characterClassIds),
				mergeCriteria(raceIds, other.raceIds),
				mergeValues(side, other.side),
				mergeValues(professionRestriction, other.professionRestriction),
				mergeValues(professionSpecId, other.professionSpecId),
				exclusiveFaction,
				activePet,
				spellId,
				mergeValues(talentId, other.talentId)
		);
	}

	@Override
	public String toString() {
		List<String> parts = new ArrayList<>();
		if (level != null) {
			parts.add(String.format("level: %s", level));
		}
		if (!characterClassIds.isEmpty()) {
			parts.add(String.format("characterClasses: %s", characterClassIds));
		}
		if (!raceIds.isEmpty()) {
			parts.add(String.format("races: %s", raceIds));
		}
		if (side != null) {
			parts.add(String.format("side: %s", side));
		}
		if (professionRestriction != null) {
			parts.add(String.format("profession: %s", professionRestriction));
		}
		if (professionSpecId != null) {
			parts.add(String.format("professionSpec: %s", professionSpecId));
		}
		if (exclusiveFaction != null) {
			parts.add(String.format("xfaction: %s", exclusiveFaction));
		}
		if (activePet != null) {
			parts.add(String.format("pet: %s", activePet));
		}
		if (spellId != null) {
			parts.add(String.format("spell: %s", spellId));
		}
		if (talentId != null) {
			parts.add(String.format("talentId: %s", talentId));
		}
		return parts.stream().collect(Collectors.joining(", ", "(", ")"));
	}
}
