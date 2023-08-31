package wow.commons.model.talent;

import lombok.Getter;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.config.impl.ConfigurationElementImpl;

/**
 * User: POlszewski
 * Date: 2022-11-28
 */
@Getter
public class TalentInfo extends ConfigurationElementImpl<TalentId> {
	private final TalentTree talentTree;
	private final int maxRank;
	private final int talentCalculatorPosition;

	public TalentInfo(
			TalentId id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			TalentTree talentTree,
			int maxRank,
			int talentCalculatorPosition
	) {
		super(id, description, timeRestriction, characterRestriction);
		this.talentTree = talentTree;
		this.maxRank = maxRank;
		this.talentCalculatorPosition = talentCalculatorPosition;
	}
}
