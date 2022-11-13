package wow.commons.model.talents;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;

/**
 * User: POlszewski
 * Date: 2020-09-30
 */
@AllArgsConstructor
@Getter
public class TalentInfo implements AttributeSource {
	private final TalentId talentId;
	private final int rank;
	private final int maxRank;
	private final String description;
	private Attributes attributes;

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
