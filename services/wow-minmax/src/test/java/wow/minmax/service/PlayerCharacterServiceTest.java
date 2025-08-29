package wow.minmax.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffCategory;
import wow.commons.model.buff.BuffId;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.model.character.BuffListType.CHARACTER_BUFF;
import static wow.commons.model.buff.BuffId.FLASK_OF_PURE_DEATH;
import static wow.commons.model.buff.BuffId.FLASK_OF_SUPREME_POWER;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
class PlayerCharacterServiceTest extends ServiceTest {
	@Test
	void enableBuff() {
		assertBuffStatus(FLASK_OF_SUPREME_POWER, false);
		assertBuffStatus(FLASK_OF_PURE_DEATH, true);

		underTest.enableBuff(CHARACTER_KEY, CHARACTER_BUFF, FLASK_OF_SUPREME_POWER, 0, true);

		assertBuffStatus(FLASK_OF_SUPREME_POWER, true);
		assertBuffStatus(FLASK_OF_PURE_DEATH, false);
	}

	@Test
	void disableBuff() {
		assertBuffStatus(FLASK_OF_PURE_DEATH, true);

		underTest.enableBuff(CHARACTER_KEY, CHARACTER_BUFF, FLASK_OF_PURE_DEATH, 0, false);

		assertBuffStatus(FLASK_OF_PURE_DEATH, false);
	}

	@Autowired
	PlayerCharacterService underTest;

	@Override
	void prepareCharacter() {
		var consumes = character.getBuffs().getList().stream()
				.filter(x -> x.getCategories().contains(BuffCategory.CONSUME))
				.map(Buff::getId)
				.toList();

		character.setBuffs(consumes);
	}

	private void assertBuffStatus(BuffId buffId, boolean enabled) {
		assertThat(savedCharacter.getBuffs().stream().anyMatch(buff -> buff.getBuffId() == buffId)).isEqualTo(enabled);
	}
}