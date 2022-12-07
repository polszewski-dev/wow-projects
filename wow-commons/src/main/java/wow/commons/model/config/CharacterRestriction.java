package wow.commons.model.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.character.*;
import wow.commons.model.professions.ProfessionSpecialization;
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
	private List<CharacterClass> characterClasses = List.of();
	@NonNull
	@Builder.Default
	private List<Race> races = List.of();
	private Side side;
	private ProfessionRestriction professionRestriction;
	private ProfessionSpecialization professionSpec;
	private TalentId talentId;

	public static final CharacterRestriction EMPTY = builder().build();

	public boolean isMetBy(CharacterInfo characterInfo) {
		return isMetBy(
				characterInfo.getLevel(),
				characterInfo.getCharacterClass(),
				characterInfo.getRace(),
				characterInfo.getBuild(),
				characterInfo.getCharacterProfessions()
		);
	}

	public boolean isMetBy(int level, CharacterClass characterClass, Race race, Build build, CharacterProfessions characterProfessions) {
		if (this.level != null && level < this.level) {
			return false;
		}
		if (!characterClasses.isEmpty() && !characterClasses.contains(characterClass)) {
			return false;
		}
		if (!this.races.isEmpty() && !this.races.contains(race)) {
			return false;
		}
		if (side != null && side != race.getSide()) {
			return false;
		}
		if (professionRestriction != null && !characterProfessions.hasProfession(professionRestriction.getProfession(), professionRestriction.getLevel())) {
			return false;
		}
		if (professionSpec != null && !characterProfessions.hasProfessionSpecialization(professionSpec)) {
			return false;
		}
		return talentId == null || build.hasTalent(talentId);
	}

	public CharacterRestriction merge(CharacterRestriction other) {
		return builder()
				.level(mergeValues(level, other.level))
				.characterClasses(mergeCriteria(characterClasses, other.characterClasses))
				.races(mergeCriteria(races, other.races))
				.side(mergeValues(side, other.side))
				.professionRestriction(mergeValues(professionRestriction, other.professionRestriction))
				.professionSpec(mergeValues(professionSpec, other.professionSpec))
				.talentId(mergeValues(talentId, other.talentId))
				.build();
	}

	@Override
	public String toString() {
		List<String> parts = new ArrayList<>();
		if (level != null) {
			parts.add(String.format("level: %s", level));
		}
		if (!characterClasses.isEmpty()) {
			parts.add(String.format("characterClasses: %s", characterClasses));
		}
		if (!races.isEmpty()) {
			parts.add(String.format("races: %s", races));
		}
		if (side != null) {
			parts.add(String.format("side: %s", side));
		}
		if (professionRestriction != null) {
			parts.add(String.format("profession: %s", professionRestriction));
		}
		if (professionSpec != null) {
			parts.add(String.format("professionSpec: %s", professionSpec));
		}
		if (talentId != null) {
			parts.add(String.format("talentId: %s", talentId));
		}
		return parts.stream().collect(Collectors.joining(", ", "(", ")"));
	}
}
