package wow.character.model.character;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.config.Described;
import wow.commons.model.config.Description;
import wow.commons.model.professions.ProfessionProficiencyId;
import wow.commons.model.professions.ProfessionType;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2023-07-31
 */
@AllArgsConstructor
@Getter
public class ProfessionProficiency implements Described {
	@NonNull
	private final ProfessionProficiencyId proficiencyId;

	@NonNull
	private final Description description;

	private final int maxSkilll;

	private final Map<ProfessionType, Integer> reqLevelByType;

	@NonNull
	private final GameVersion gameVersion;

	public int getReqLevel(ProfessionType type) {
		return reqLevelByType.get(type);
	}
}
