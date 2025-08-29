package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.buff.BuffId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.model.character.BuffListType.CHARACTER_BUFF;
import static wow.commons.model.buff.BuffId.FLASK_OF_PURE_DEATH;
import static wow.commons.model.buff.BuffId.FLASK_OF_SUPREME_POWER;

/**
 * User: POlszewski
 * Date: 2025-08-29
 */
class BuffServiceTest extends ServiceTest {
	@Test
	void getBuffStatuses() {
		var buffStatuses = underTest.getBuffStatuses(CHARACTER_KEY, CHARACTER_BUFF);

		var statusStrings = buffStatuses.stream()
				.map(x -> x.buff().getBuffId() + "#" + x.enabled())
				.sorted()
				.toList();

		assertThat(statusStrings).hasSameElementsAs(List.of(
				"Arcane Brilliance#true",
				"Brilliant Wizard Oil#true",
				"Burning Wish#false",
				"Demon Armor#false",
				"Drums of Battle#false",
				"Fel Armor#true",
				"Flask of Pure Death#true",
				"Flask of Supreme Power#false",
				"Gift of the Wild#true",
				"Greater Blessing of Kings#true",
				"Moonkin Aura#false",
				"Prayer of Fortitude#true",
				"Prayer of Spirit#true",
				"Superior Wizard Oil#false",
				"Totem of Wrath#true",
				"Touch of Shadow#true",
				"Well Fed (sp)#true",
				"Wrath of Air Totem#true"
		));
	}

	@Test
	void enableBuff() {
		assertBuffStatus(FLASK_OF_SUPREME_POWER, false);
		assertBuffStatus(FLASK_OF_PURE_DEATH, true);

		underTest.changeBuffStatus(CHARACTER_KEY, CHARACTER_BUFF, FLASK_OF_SUPREME_POWER, 0, true);

		assertBuffStatus(FLASK_OF_SUPREME_POWER, true);
		assertBuffStatus(FLASK_OF_PURE_DEATH, false);
	}

	@Test
	void disableBuff() {
		assertBuffStatus(FLASK_OF_PURE_DEATH, true);

		underTest.changeBuffStatus(CHARACTER_KEY, CHARACTER_BUFF, FLASK_OF_PURE_DEATH, 0, false);

		assertBuffStatus(FLASK_OF_PURE_DEATH, false);
	}

	@Autowired
	BuffService underTest;

	private void assertBuffStatus(BuffId buffId, boolean enabled) {
		assertThat(savedCharacter.getBuffs().stream().anyMatch(buff -> buff.getBuffId() == buffId)).isEqualTo(enabled);
	}
}
