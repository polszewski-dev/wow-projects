package wow.commons.model.talents;

import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.ConfigurationElementWithAttributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2020-09-30
 */
@Getter
public class Talent extends ConfigurationElementWithAttributes<TalentIdAndRank> {
	@NonNull
	private final TalentInfo talentInfo;

	public Talent(TalentIdAndRank id, Description description, TimeRestriction timeRestriction, CharacterRestriction characterRestriction, Attributes attributes, TalentInfo talentInfo) {
		super(id, description, timeRestriction, characterRestriction, attributes);
		this.talentInfo = talentInfo;
	}

	public TalentId getTalentId() {
		return getId().getTalentId();
	}

	public int getRank() {
		return getId().getRank();
	}

	public int getMaxRank() {
		return talentInfo.getMaxRank();
	}

	public int getTalentCalculatorPosition() {
		return talentInfo.getTalentCalculatorPosition();
	}

	public Talent combineWith(Talent talent) {
		Attributes combinedAttributes = AttributesBuilder.addAttributes(getAttributes(), talent.getAttributes());
		return new Talent(getId(), getDescription(), getTimeRestriction(), getCharacterRestriction(), combinedAttributes, talentInfo);
	}

	@Override
	public String toString() {
		if (getMaxRank() == 1) {
			return getName();
		} else {
			return String.format("%s %s/%s", getName(), getRank(), getMaxRank());
		}
	}
}
