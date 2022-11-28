package wow.commons.model.talents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;

/**
 * User: POlszewski
 * Date: 2022-11-28
 */
@AllArgsConstructor
@Getter
public class TalentInfo {
	private final TalentId talentId;
	private final Description description;
	private final Restriction restriction;
	private final TalentTree talentTree;
	private final int maxRank;
	private final int talentCalculatorPosition;
}
