package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.pve.PhaseId;

/**
 * User: POlszewski
 * Date: 2023-03-30
 */
@AllArgsConstructor
@Getter
public class Phase implements Described {
	@NonNull
	private final PhaseId phaseId;

	@NonNull
	private final Description description;

	@NonNull
	private final GameVersion gameVersion;

	@NonNull
	public GameVersionId getGameVersionId() {
		return gameVersion.getGameVersionId();
	}

	@Override
	public String toString() {
		return getName();
	}
}
