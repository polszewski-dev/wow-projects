package wow.character.model.talent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.TestEvent;
import wow.character.model.character.TestPlayerCharacter;
import wow.commons.model.talent.Talent;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.model.character.TestEvent.TalentDisabled;
import static wow.character.model.character.TestEvent.TalentEnabled;
import static wow.test.commons.TalentNames.*;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
class TalentsTest extends WowCharacterSpringTest {
	@Test
	void loadFromTalentLink() {
		talents.loadFromTalentLink("https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303024");

		assertThat(talents.getList().stream().map(x -> x.getName() + "#" + x.getRank())).hasSameElementsAs(List.of(
				"Improved Healthstone#2",
				"Demonic Embrace#5",
				"Improved Voidwalker#1",
				"Fel Intellect#3",
				"Fel Domination#1",
				"Fel Stamina#3",
				"Demonic Aegis#3",
				"Master Summoner#2",
				"Demonic Sacrifice#1",
				"Improved Shadow Bolt#5",
				"Cataclysm#5",
				"Bane#5",
				"Devastation#5",
				"Shadowburn#1",
				"Intensity#2",
				"Destructive Reach#2",
				"Improved Searing Pain#1",
				"Ruin#1",
				"Nether Protection#3",
				"Backlash#3",
				"Soul Leech#2",
				"Shadow and Flame#4"
		));
	}

	@Test
	void has() {
		talents.loadFromTalentLink("https://www.wowhead.com/tbc/talent-calc/warlock/-20501301332001-55500051221001303024");

		assertThat(talents.has(DEMONIC_SACRIFICE)).isTrue();
		assertThat(talents.has(DEMONIC_SACRIFICE, 1)).isTrue();
		assertThat(talents.has(IMPROVED_VOIDWALKER)).isTrue();
		assertThat(talents.has(IMPROVED_VOIDWALKER, 1)).isTrue();
		assertThat(talents.has(IMPROVED_VOIDWALKER, 2)).isFalse();
		assertThat(talents.has(SHADOW_MASTERY)).isFalse();
		assertThat(talents.has(SHADOW_MASTERY, 1)).isFalse();
	}

	@Test
	void enable_talent() {
		talents.enable(IMPROVED_CORRUPTION, 1);

		assertEvents(
				new TalentEnabled(talentRank1)
		);
	}

	@Test
	void disable_talent() {
		talents.enable(IMPROVED_CORRUPTION, 1);
		talents.disable(IMPROVED_CORRUPTION);

		assertEvents(
				new TalentEnabled(talentRank1),
				new TalentDisabled(talentRank1)
		);
	}

	@Test
	void disable_not_enabled_talent() {
		talents.disable(IMPROVED_CORRUPTION);

		assertEvents();
	}

	@Test
	void reset_talent() {
		talents.enable(IMPROVED_CORRUPTION, 1);
		talents.reset();

		assertEvents(
				new TalentEnabled(talentRank1),
				new TalentDisabled(talentRank1)
		);
	}

	@Test
	void enable_consecutive_ranks() {
		talents.enable(IMPROVED_CORRUPTION, 1);
		talents.enable(IMPROVED_CORRUPTION, 2);
		talents.enable(IMPROVED_CORRUPTION, 3);

		assertEvents(
				new TalentEnabled(talentRank1),
				new TalentDisabled(talentRank1),
				new TalentEnabled(talentRank2),
				new TalentDisabled(talentRank2),
				new TalentEnabled(talentRank3)
		);
	}

	TestPlayerCharacter player;
	Talents talents;

	Talent talentRank1;
	Talent talentRank2;
	Talent talentRank3;
	Talent talentRank4;
	Talent talentRank5;

	@BeforeEach
	void setup() {
		player = getPlayer();
		talents = player.getTalents();
		talents.reset();
		resetEvents();

		talentRank1 = talents.getTalent(2, 1).orElseThrow();
		talentRank2 = talents.getTalent(2, 2).orElseThrow();
		talentRank3 = talents.getTalent(2, 3).orElseThrow();
		talentRank4 = talents.getTalent(2, 4).orElseThrow();
		talentRank5 = talents.getTalent(2, 5).orElseThrow();
	}

	void assertEvents(TestEvent... expected) {
		assertThat(player.events).isEqualTo(List.of(expected));
	}

	void resetEvents() {
		player.events.clear();
	}
}