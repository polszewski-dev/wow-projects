package wow.commons.model.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.character.RaceId;
import wow.commons.model.professions.ProfessionSpecializationId;
import wow.commons.model.pve.Side;
import wow.commons.model.talents.TalentId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static wow.commons.util.CollectionUtil.mergeCriteria;
import static wow.commons.util.CollectionUtil.mergeValues;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
@Builder
public class CharacterRestriction {
	private Integer level;
	@NonNull
	@Builder.Default
	private List<CharacterClassId> characterClassIds = List.of();
	@NonNull
	@Builder.Default
	private List<RaceId> raceIds = List.of();
	private Side side;
	private ProfessionRestriction professionRestriction;
	private ProfessionSpecializationId professionSpecId;
	private TalentId talentId;

	public static final CharacterRestriction EMPTY = builder().build();

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
		if (side != null && side != characterInfo.getRaceId().getSide()) {
			return false;
		}
		if (professionRestriction != null && !characterInfo.hasProfession(professionRestriction.getProfessionId())) {
			return false;
		}
		if (professionSpecId != null && !characterInfo.hasProfessionSpecialization(professionSpecId)) {
			return false;
		}
		return talentId == null || characterInfo.hasTalent(talentId);
	}

	public CharacterRestriction merge(CharacterRestriction other) {
		return builder()
				.level(mergeValues(level, other.level))
				.characterClassIds(mergeCriteria(characterClassIds, other.characterClassIds))
				.raceIds(mergeCriteria(raceIds, other.raceIds))
				.side(mergeValues(side, other.side))
				.professionRestriction(mergeValues(professionRestriction, other.professionRestriction))
				.professionSpecId(mergeValues(professionSpecId, other.professionSpecId))
				.talentId(mergeValues(talentId, other.talentId))
				.build();
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
		if (talentId != null) {
			parts.add(String.format("talentId: %s", talentId));
		}
		return parts.stream().collect(Collectors.joining(", ", "(", ")"));
	}
}
