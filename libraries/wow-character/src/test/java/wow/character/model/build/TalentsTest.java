package wow.character.model.build;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.PlayerCharacter;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.test.commons.TalentNames.*;

/**
 * User: POlszewski
 * Date: 2023-05-11
 */
class TalentsTest extends WowCharacterSpringTest {
	@Test
	void copy() {
		Talents copy = talents.copy();

		talents.reset();

		assertThat(copy.getList()).isNotEmpty();
	}

	@Test
	void reset() {
		talents.reset();

		assertThat(talents.getList()).isEmpty();
	}

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
		assertThat(talents.has(DEMONIC_SACRIFICE)).isTrue();
		assertThat(talents.has(DEMONIC_SACRIFICE, 1)).isTrue();
		assertThat(talents.has(IMPROVED_VOIDWALKER)).isTrue();
		assertThat(talents.has(IMPROVED_VOIDWALKER, 1)).isTrue();
		assertThat(talents.has(IMPROVED_VOIDWALKER, 2)).isFalse();
		assertThat(talents.has(SHADOW_MASTERY)).isFalse();
		assertThat(talents.has(SHADOW_MASTERY, 1)).isFalse();
	}

	PlayerCharacter character;
	Talents talents;

	@BeforeEach
	void setup() {
		character = getCharacter();
		talents = character.getTalents();
	}
}