package wow.commons.model.talent;

import wow.commons.model.character.CharacterClassId;
import wow.commons.model.config.CharacterRestricted;
import wow.commons.model.config.Described;
import wow.commons.model.config.TimeRestricted;
import wow.commons.model.effect.Effect;

/**
 * User: POlszewski
 * Date: 2020-09-30
 */
public interface Talent extends Described, TimeRestricted, CharacterRestricted {
	TalentId getId();

	int getRank();

	int getMaxRank();

	int getTalentCalculatorPosition();

	TalentNameRank getNameRank();

	CharacterClassId getCharacterClass();

	TalentTree getTalentTree();

	Effect getEffect();
}
