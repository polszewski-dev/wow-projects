package wow.commons.model.talents;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.ConfigurationElementWithAttributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;

/**
 * User: POlszewski
 * Date: 2020-09-30
 */
@Getter
public class Talent extends ConfigurationElementWithAttributes<TalentIdAndRank> {
	private final int maxRank;
	private final int talentCalculatorPosition;
	private Attributes attributes;

	public Talent(TalentIdAndRank id, Description description, Restriction restriction, Attributes attributes, int maxRank, int talentCalculatorPosition) {
		super(id, description, restriction, attributes);
		this.maxRank = maxRank;
		this.talentCalculatorPosition = talentCalculatorPosition;
	}

	public TalentId getTalentId() {
		return getId().getTalentId();
	}

	public int getRank() {
		return getId().getRank();
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
			return getName();
		} else {
			return String.format("%s %s/%s", getName(), getRank(), maxRank);
		}
	}
}
