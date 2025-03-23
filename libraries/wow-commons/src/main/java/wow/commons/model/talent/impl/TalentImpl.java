package wow.commons.model.talent.impl;

import lombok.Getter;
import lombok.Setter;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.effect.Effect;
import wow.commons.model.talent.Talent;
import wow.commons.model.talent.TalentId;
import wow.commons.model.talent.TalentIdAndRank;
import wow.commons.model.talent.TalentTree;

import static wow.commons.util.CollectionUtil.getUniqueResult;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
@Setter
public class TalentImpl implements Talent {
	private final int id;
	private final int rank;
	private final int maxRank;
	private final int talentCalculatorPosition;
	private final TalentTree talentTree;
	private final TalentIdAndRank rankedTalentId;
	private final Description description;
	private final TimeRestriction timeRestriction;
	private final CharacterRestriction characterRestriction;
	private Effect effect;

	public TalentImpl(
			int id,
			int rank,
			int maxRank,
			int talentCalculatorPosition,
			TalentTree talentTree,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction
	) {
		this.id = id;
		this.rank = rank;
		this.maxRank = maxRank;
		this.talentCalculatorPosition = talentCalculatorPosition;
		this.talentTree = talentTree;
		this.rankedTalentId = new TalentIdAndRank(TalentId.parse(description.name()), rank);
		this.description = description;
		this.timeRestriction = timeRestriction;
		this.characterRestriction = characterRestriction;
	}

	@Override
	public CharacterClassId getCharacterClass() {
		return getUniqueResult(getRequiredCharacterClassIds()).orElseThrow();
	}

	@Override
	public String toString() {
		if (getMaxRank() == 1) {
			return getName();
		} else {
			return rankedTalentId.toString();
		}
	}
}
