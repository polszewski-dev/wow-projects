package wow.character.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import wow.character.WowCharacterSpringTest;
import wow.character.model.build.TalentLink;
import wow.character.model.build.TalentLinkType;
import wow.commons.model.character.CharacterClassId;
import wow.commons.model.pve.GameVersionId;
import wow.commons.model.talent.TalentIdAndRank;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * User: POlszewski
 * Date: 2023-12-10
 */
class TalentLinkParserTest extends WowCharacterSpringTest {
	@Test
	void legacyWow() {
		var linkString = "https://legacy-wow.com/tbc-talents/warlock-talents/?tal=0000000000000000000002050130133200100000000555000512210013030250";
		var link = TalentLinkParser.parse(linkString, spellRepository);

		assertThat(link.link()).isEqualTo(linkString);
		assertThat(link.type()).isEqualTo(TalentLinkType.LEGACY_WOW);
		assertThat(link.gameVersionId()).isEqualTo(GameVersionId.TBC);
		assertThat(link.characterClassId()).isEqualTo(CharacterClassId.WARLOCK);

		assertTalents(link, List.of(
				"Improved Healthstone (Rank 2)",
				"Demonic Embrace (Rank 5)",
				"Improved Voidwalker (Rank 1)",
				"Fel Intellect (Rank 3)",
				"Fel Domination (Rank 1)",
				"Fel Stamina (Rank 3)",
				"Demonic Aegis (Rank 3)",
				"Master Summoner (Rank 2)",
				"Demonic Sacrifice (Rank 1)",
				"Improved Shadow Bolt (Rank 5)",
				"Cataclysm (Rank 5)",
				"Bane (Rank 5)",
				"Devastation (Rank 5)",
				"Shadowburn (Rank 1)",
				"Intensity (Rank 2)",
				"Destructive Reach (Rank 2)",
				"Improved Searing Pain (Rank 1)",
				"Ruin (Rank 1)",
				"Nether Protection (Rank 3)",
				"Backlash (Rank 3)",
				"Soul Leech (Rank 2)",
				"Shadow and Flame (Rank 5)"
		));
	}

	@Test
	void wowhead() {
		var linkString = "https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303025";
		var link = TalentLinkParser.parse(linkString, spellRepository);

		assertThat(link.link()).isEqualTo(linkString);
		assertThat(link.type()).isEqualTo(TalentLinkType.WOWHEAD);
		assertThat(link.gameVersionId()).isEqualTo(GameVersionId.TBC);
		assertThat(link.characterClassId()).isEqualTo(CharacterClassId.WARLOCK);

		assertTalents(link, List.of(
				"Improved Healthstone (Rank 2)",
				"Demonic Embrace (Rank 5)",
				"Improved Voidwalker (Rank 1)",
				"Fel Intellect (Rank 3)",
				"Fel Domination (Rank 1)",
				"Fel Stamina (Rank 3)",
				"Demonic Aegis (Rank 3)",
				"Master Summoner (Rank 2)",
				"Demonic Sacrifice (Rank 1)",
				"Improved Shadow Bolt (Rank 5)",
				"Cataclysm (Rank 5)",
				"Bane (Rank 5)",
				"Devastation (Rank 5)",
				"Shadowburn (Rank 1)",
				"Intensity (Rank 2)",
				"Destructive Reach (Rank 2)",
				"Improved Searing Pain (Rank 1)",
				"Ruin (Rank 1)",
				"Nether Protection (Rank 3)",
				"Backlash (Rank 3)",
				"Soul Leech (Rank 2)",
				"Shadow and Flame (Rank 5)"
		));
	}

	@ParameterizedTest
	@MethodSource
	void wowheadSpecialCases(String linkString, List<String> talentStrings) {
		var link = TalentLinkParser.parse(linkString, spellRepository);

		assertTalents(link, talentStrings);
	}

	static Stream<Arguments> wowheadSpecialCases() {
		return Stream.of(
				Arguments.of("https://www.wowhead.com/tbc/talent-calc/warlock", List.of()),
				Arguments.of("https://www.wowhead.com/tbc/talent-calc/warlock/-", List.of()),
				Arguments.of("https://www.wowhead.com/tbc/talent-calc/warlock/--", List.of()),
				Arguments.of("https://www.wowhead.com/tbc/talent-calc/warlock/1", List.of(
						"Suppression (Rank 1)"
				)),
				Arguments.of("https://www.wowhead.com/tbc/talent-calc/warlock/-2", List.of(
						"Improved Healthstone (Rank 2)"
				)),
				Arguments.of("https://www.wowhead.com/tbc/talent-calc/warlock/--3", List.of(
						"Improved Shadow Bolt (Rank 3)"
				)),
				Arguments.of("https://www.wowhead.com/tbc/talent-calc/warlock/1--3", List.of(
						"Suppression (Rank 1)",
						"Improved Shadow Bolt (Rank 3)"
				)),
				Arguments.of("https://www.wowhead.com/tbc/talent-calc/warlock/-2-3", List.of(
						"Improved Healthstone (Rank 2)",
						"Improved Shadow Bolt (Rank 3)"
				)),
				Arguments.of("https://www.wowhead.com/tbc/talent-calc/warlock/1-2", List.of(
						"Suppression (Rank 1)",
						"Improved Healthstone (Rank 2)"
				)),
				Arguments.of("https://www.wowhead.com/tbc/talent-calc/warlock/1-2-3", List.of(
						"Suppression (Rank 1)",
						"Improved Healthstone (Rank 2)",
						"Improved Shadow Bolt (Rank 3)"
				))
		);
	}

	private static void assertTalents(TalentLink link, List<String> expected) {
		var actual = link.talents().stream()
				.map(TalentIdAndRank::toString)
				.toList();
		assertThat(actual).isEqualTo(expected);
	}
}