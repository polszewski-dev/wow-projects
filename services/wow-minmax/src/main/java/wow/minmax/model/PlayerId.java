package wow.minmax.model;

import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.PhaseId;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
public record PlayerId(
		UUID profileId,
		PhaseId phaseId,
		int level,
		CreatureType enemyType,
		int enemyLevelDiff
) {
	private static final String SEPARATOR = ",";

	public static PlayerId parse(String value) {
		if (value == null) {
			return null;
		}
		var parts = value.split(SEPARATOR);
		return new PlayerId(
				UUID.fromString(parts[0]),
				PhaseId.parse(parts[1]),
				Integer.parseInt(parts[2]),
				CreatureType.parse(parts[3]),
				Integer.parseInt(parts[4])
		);
	}

	public PlayerId getPreviousPhasePlayerId() {
		return new PlayerId(
				profileId,
				phaseId.getPreviousPhase().orElseThrow(),
				level,
				enemyType,
				enemyLevelDiff
		);
	}

	@Override
	public String toString() {
		return Stream.of(profileId, phaseId, level, enemyType, enemyLevelDiff)
				.map(Objects::toString)
				.collect(Collectors.joining(SEPARATOR))
				.toLowerCase();
	}
}
