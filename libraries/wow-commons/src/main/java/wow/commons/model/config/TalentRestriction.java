package wow.commons.model.config;

import wow.commons.model.talent.TalentNameRank;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-03-25
 */
public sealed interface TalentRestriction {
	String talentName();

	boolean isMetBy(CharacterInfo characterInfo);

	record TalentIdRestriction(String talentName) implements TalentRestriction {
		public TalentIdRestriction {
			Objects.requireNonNull(talentName);
		}

		@Override
		public boolean isMetBy(CharacterInfo characterInfo) {
			return characterInfo.hasTalent(talentName);
		}
	}

	record TalentIdAndRankRestriction(TalentNameRank talentNameRank) implements TalentRestriction {
		public TalentIdAndRankRestriction {
			Objects.requireNonNull(talentNameRank);
		}

		@Override
		public String talentName() {
			return talentNameRank.name();
		}

		@Override
		public boolean isMetBy(CharacterInfo characterInfo) {
			return characterInfo.hasTalent(talentNameRank.name(), talentNameRank.rank());
		}
	}

	static TalentRestriction of(String talentName) {
		if (talentName == null) {
			return null;
		}
		return new TalentIdRestriction(talentName);
	}

	static TalentRestriction of(String talentName, Integer rank) {
		if (talentName == null && rank == null) {
			return null;
		}
		if (rank != null) {
			return new TalentIdAndRankRestriction(new TalentNameRank(talentName, rank));
		} else {
			return new TalentIdRestriction(talentName);
		}
	}
}
