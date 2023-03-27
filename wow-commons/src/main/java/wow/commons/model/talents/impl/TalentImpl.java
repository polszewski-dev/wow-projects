package wow.commons.model.talents.impl;

import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.character.CharacterClass;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.config.impl.ConfigurationElementWithAttributesImpl;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentIdAndRank;
import wow.commons.model.talents.TalentInfo;
import wow.commons.util.AttributesBuilder;
import wow.commons.util.CollectionUtil;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public class TalentImpl extends ConfigurationElementWithAttributesImpl<TalentIdAndRank> implements Talent {
	@NonNull
	private final TalentInfo talentInfo;

	public TalentImpl(
			TalentIdAndRank id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			Attributes attributes,
			TalentInfo talentInfo
	) {
		super(id, description, timeRestriction, characterRestriction, attributes);
		this.talentInfo = talentInfo;
	}

	@Override
	public CharacterClass getCharacterClass() {
		return CollectionUtil.getUniqueResult(getCharacterRestriction().getCharacterClasses()).orElseThrow();
	}

	@Override
	public Talent combineWith(Talent talent) {
		Attributes combinedAttributes = AttributesBuilder.addAttributes(getAttributes(), talent.getAttributes());
		return new TalentImpl(getId(), getDescription(), getTimeRestriction(), getCharacterRestriction(), combinedAttributes, talentInfo);
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
