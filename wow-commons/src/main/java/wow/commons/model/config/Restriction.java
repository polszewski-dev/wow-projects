package wow.commons.model.config;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.character.*;
import wow.commons.model.professions.ProfessionSpecialization;
import wow.commons.model.pve.GameVersion;
import wow.commons.model.pve.Phase;
import wow.commons.model.pve.Side;
import wow.commons.model.talents.TalentId;
import wow.commons.util.CollectionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2022-10-31
 */
@Getter
@Builder
public class Restriction {
	@NonNull
	@Builder.Default
	private List<GameVersion> versions = List.of();
	private Phase phase;
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

	public boolean isMetBy(CharacterInfo characterInfo) {
		return isMetBy(
				characterInfo.getGameVersion(),
				characterInfo.getPhase(),
				characterInfo.getLevel(),
				characterInfo.getCharacterClass(),
				characterInfo.getRace(),
				characterInfo.getBuild(),
				characterInfo.getCharacterProfessions()
		);
	}

	public boolean isMetBy(GameVersion version, Phase phase, int level, CharacterClass characterClass, Race race, Build build, CharacterProfessions characterProfessions) {
		if (!this.versions.isEmpty() && !this.versions.contains(version)) {
			return false;
		}
		if (this.phase != null && phase.isEarlier(this.phase)) {
			return false;
		}
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

	public Restriction merge(Restriction other) {
		return builder()
				.versions(merge(versions, other.versions))
				.phase(merge(phase, other.phase))
				.level(merge(level, other.level))
				.characterClasses(merge(characterClasses, other.characterClasses))
				.races(merge(races, other.races))
				.side(merge(side, other.side))
				.professionRestriction(merge(professionRestriction, other.professionRestriction))
				.professionSpec(merge(professionSpec, other.professionSpec))
				.talentId(merge(talentId, other.talentId))
				.build();
	}

	private <T> T merge(T first, T second) {
		if (first == null) {
			return second;
		}
		if (second == null) {
			return first;
		}
		if (Objects.equals(first, second)) {
			return first;
		}
		throw new IllegalArgumentException(String.format("Both elements are not null: first=%s, second=%s", first, second));
	}

	private <T> List<T> merge(List<T> first, List<T> second) {
		return CollectionUtil.getCommonCriteria(first, second)
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Both lists have no common elements: first=%s, second=%s", first, second)));
	}

	@Override
	public String toString() {
		List<String> parts = new ArrayList<>();
		if (level != null) {
			parts.add(String.format("level: %s", level));
		}
		if (!versions.isEmpty()) {
			parts.add(String.format("versions: %s", versions));
		}
		if (phase != null) {
			parts.add(String.format("phase: %s", phase));
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
