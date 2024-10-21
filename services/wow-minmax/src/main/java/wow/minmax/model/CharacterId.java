package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.character.CreatureType;
import wow.commons.model.pve.PhaseId;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2023-04-01
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class CharacterId implements Serializable {
	private final UUID profileId;
	private final PhaseId phaseId;
	private final int level;
	private final CreatureType enemyType;
	private final int enemyLevelDiff;

	private static final String SEPARATOR = ",";

	public static CharacterId parse(String value) {
		if (value == null) {
			return null;
		}
		String[] parts = value.split(SEPARATOR);
		return new CharacterId(
				UUID.fromString(parts[0]),
				PhaseId.parse(parts[1]),
				Integer.parseInt(parts[2]),
				CreatureType.parse(parts[3]),
				Integer.parseInt(parts[4])
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
