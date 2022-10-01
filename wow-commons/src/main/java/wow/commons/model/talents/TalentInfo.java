package wow.commons.model.talents;

import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;

/**
 * User: POlszewski
 * Date: 2020-09-30
 */
public class TalentInfo implements AttributeSource {
	private final TalentId talentId;
	private final int rank;
	private final int maxRank;
	private final String description;

	private Attributes attributes;

	public TalentInfo(TalentId talentId, int rank, int maxRank, String description, Attributes attributes) {
		this.talentId = talentId;
		this.rank = rank;
		this.maxRank = maxRank;
		this.description = description;
		this.attributes = attributes;
	}

	public TalentId getTalentId() {
		return talentId;
	}

	public int getRank() {
		return rank;
	}

	public int getMaxRank() {
		return maxRank;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public Attributes getAttributes() {
		return attributes;
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		if (maxRank == 1) {
			return talentId.getName();
		} else {
			return String.format("%s %s/%s", talentId.getName(), rank, maxRank);
		}
	}
}
