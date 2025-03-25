package wow.commons.model.config;

import wow.commons.model.talent.TalentId;
import wow.commons.model.talent.TalentIdAndRank;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-03-25
 */
public sealed interface TalentRestriction {
	TalentId talentId();

	boolean isMetBy(CharacterInfo characterInfo);

	record TalentIdRestriction(TalentId talentId) implements TalentRestriction {
		public TalentIdRestriction {
			Objects.requireNonNull(talentId);
		}

		@Override
		public boolean isMetBy(CharacterInfo characterInfo) {
			return characterInfo.hasTalent(talentId);
		}
	}

	record TalentIdAndRankRestriction(TalentIdAndRank talentIdAndRank) implements TalentRestriction {
		public TalentIdAndRankRestriction {
			Objects.requireNonNull(talentIdAndRank);
		}

		@Override
		public TalentId talentId() {
			return talentIdAndRank.talentId();
		}

		@Override
		public boolean isMetBy(CharacterInfo characterInfo) {
			return characterInfo.hasTalent(talentIdAndRank.talentId(), talentIdAndRank.rank());
		}
	}

	static TalentRestriction of(TalentId talentId) {
		if (talentId == null) {
			return null;
		}
		return new TalentIdRestriction(talentId);
	}

	static TalentRestriction of(TalentId talentId, Integer rank) {
		if (talentId == null && rank == null) {
			return null;
		}
		if (rank != null) {
			return new TalentIdAndRankRestriction(new TalentIdAndRank(talentId, rank));
		} else {
			return new TalentIdRestriction(talentId);
		}
	}
}
