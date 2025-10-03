package wow.character.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import wow.character.WowCharacterSpringTest;
import wow.character.model.build.Talents;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.character.CharacterClassId.WARLOCK;
import static wow.commons.model.pve.PhaseId.TBC_P5;

/**
 * User: POlszewski
 * Date: 2023-12-10
 */
class TalentLinkFormatterTest extends WowCharacterSpringTest {
	@ParameterizedTest
	@ValueSource(strings = {
			"https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025",
			"https://www.wowhead.com/tbc/talent-calc/warlock",
			"https://www.wowhead.com/tbc/talent-calc/warlock/1",
			"https://www.wowhead.com/tbc/talent-calc/warlock/-2",
			"https://www.wowhead.com/tbc/talent-calc/warlock/--3",
			"https://www.wowhead.com/tbc/talent-calc/warlock/1--3",
			"https://www.wowhead.com/tbc/talent-calc/warlock/-2-3",
			"https://www.wowhead.com/tbc/talent-calc/warlock/1-2",
			"https://www.wowhead.com/tbc/talent-calc/warlock/1-2-3",

	})
	void wowhead(String expected) {
		var talentLinkFormatter = getTalentLinkFormatter(expected);

		var actual = talentLinkFormatter.format();

		assertThat(actual).isEqualTo(expected);
	}

	private TalentLinkFormatter getTalentLinkFormatter(String expected) {
		var parsed = TalentLinkParser.parse(expected, talentRepository);
		var characterClassId = parsed.characterClassId();
		var gameVersionId = parsed.gameVersionId();
		var talents = new Talents(WARLOCK, TBC_P5, talentRepository.getAvailableTalents(characterClassId, gameVersionId.getLastPhase()));

		for (var nameRank : parsed.talents()) {
			talents.enable(nameRank.name(), nameRank.rank());
		}

		return new TalentLinkFormatter(talents, gameVersionId, characterClassId);
	}
}