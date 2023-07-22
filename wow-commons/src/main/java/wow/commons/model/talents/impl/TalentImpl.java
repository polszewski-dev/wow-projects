package wow.commons.model.talents.impl;

import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.attributes.complex.special.SpecialAbilitySource;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.config.impl.ConfigurationElementWithAttributesImpl;
import wow.commons.model.talents.Talent;
import wow.commons.model.talents.TalentIdAndRank;
import wow.commons.model.talents.TalentInfo;
import wow.commons.model.talents.TalentSource;
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
			TalentInfo talentInfo
	) {
		super(id, description, timeRestriction, characterRestriction);
		this.talentInfo = talentInfo;
	}

	@Override
	public CharacterClassId getCharacterClass() {
		return CollectionUtil.getUniqueResult(getCharacterRestriction().characterClassIds()).orElseThrow();
	}

	@Override
	public Talent combineWith(Talent talent) {
		var combinedAttributes = AttributesBuilder.addAttributes(getAttributes(), talent.getAttributes());
		var result = new TalentImpl(getId(), getDescription(), getTimeRestriction(), getCharacterRestriction(), talentInfo);
		result.setAttributes(combinedAttributes);
		return result;
	}

	@Override
	protected SpecialAbilitySource getSpecialAbilitySource() {
		return new TalentSource(this);
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
