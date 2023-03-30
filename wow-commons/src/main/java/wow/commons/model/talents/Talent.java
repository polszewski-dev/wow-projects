package wow.commons.model.talents;

import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.ConfigurationElementWithAttributes;

/**
 * User: POlszewski
 * Date: 2020-09-30
 */
public interface Talent extends ConfigurationElementWithAttributes<TalentIdAndRank> {
	TalentInfo getTalentInfo();

	default TalentId getTalentId() {
		return getId().getTalentId();
	}

	default int getRank() {
		return getId().getRank();
	}

	default int getMaxRank() {
		return getTalentInfo().getMaxRank();
	}

	default int getTalentCalculatorPosition() {
		return getTalentInfo().getTalentCalculatorPosition();
	}

	CharacterClassId getCharacterClass();

	Talent combineWith(Talent talent);
}
