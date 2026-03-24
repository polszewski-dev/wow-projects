package wow.character.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffId;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static wow.test.commons.BuffNames.*;

/**
 * User: POlszewski
 * Date: 2023-08-03
 */
class BuffsTest extends WowCharacterSpringTest {
	@Test
	void copy() {
		buffs.enable(FLASK_OF_SUPREME_POWER);

		assertBuffs(FLASK_OF_SUPREME_POWER);

		var copy = buffs.copy();
		buffs.reset();

		assertNoBuffs();
		assertBuffs(copy, FLASK_OF_SUPREME_POWER);
	}

	@Test
	void getList() {
		assertNoBuffs();

		buffs.enable(FLASK_OF_SUPREME_POWER);

		assertBuffs(FLASK_OF_SUPREME_POWER);

		buffs.disable(FLASK_OF_SUPREME_POWER);

		assertNoBuffs();
	}

	@Test
	void getAvailableHighestRanks() {
		var names = buffs.getAvailableStream()
				.map(Buff::getName)
				.toList();

		assertThat(names).hasSameElementsAs(List.of(
				BRILLIANT_WIZARD_OIL,
				SUPERIOR_WIZARD_OIL,
				WELL_FED_SP,
				FLASK_OF_SUPREME_POWER,
				FLASK_OF_PURE_DEATH,
				FLASK_OF_BLINDING_LIGHT,
				DRUMS_OF_BATTLE
		));
	}

	@Test
	void reset() {
		assertBuff(FLASK_OF_SUPREME_POWER, false);

		buffs.enable(FLASK_OF_SUPREME_POWER);

		assertBuff(FLASK_OF_SUPREME_POWER, true);

		buffs.reset();

		assertBuff(FLASK_OF_SUPREME_POWER, false);

		buffs.enable(FLASK_OF_SUPREME_POWER);

		assertBuff(FLASK_OF_SUPREME_POWER, true);
	}

	@Test
	void setNames() {
		assertBuff(FLASK_OF_PURE_DEATH, false);
		assertBuff(FLASK_OF_SUPREME_POWER, false);

		buffs.setNames(List.of(BRILLIANT_WIZARD_OIL, FLASK_OF_SUPREME_POWER));

		assertBuff(BRILLIANT_WIZARD_OIL, true);
		assertBuff(FLASK_OF_SUPREME_POWER, true);
		assertBuffs(BRILLIANT_WIZARD_OIL, FLASK_OF_SUPREME_POWER);

		buffs.setNames(List.of(FLASK_OF_PURE_DEATH));

		assertBuffs(FLASK_OF_PURE_DEATH);
	}

	@Test
	void set() {
		assertBuff(FLASK_OF_SUPREME_POWER, false);

		buffs.setNames(List.of(FLASK_OF_SUPREME_POWER));

		assertBuff(FLASK_OF_SUPREME_POWER, true);
		assertBuffs(FLASK_OF_SUPREME_POWER);

		var invalidIds = List.of(BuffId.of(-1));

		assertThatThrownBy(() -> buffs.setIds(invalidIds)).isInstanceOf(NoSuchElementException.class);
		assertNoBuffs();
	}

	@Test
	void enable() {
		assertBuff(FLASK_OF_SUPREME_POWER, false);

		buffs.enable(FLASK_OF_SUPREME_POWER);

		assertBuffs(FLASK_OF_SUPREME_POWER);

		assertThatThrownBy(() -> buffs.enable(BuffId.of(-1))).isInstanceOf(NoSuchElementException.class);

		assertThat(buffs.getList()).hasSize(1);
		assertBuffs(FLASK_OF_SUPREME_POWER);
	}

	@Test
	void exclusionGroups() {
		assertBuff(FLASK_OF_SUPREME_POWER, false);
		assertBuff(FLASK_OF_PURE_DEATH, false);

		buffs.enable(FLASK_OF_SUPREME_POWER);

		assertBuff(FLASK_OF_SUPREME_POWER, true);
		assertBuff(FLASK_OF_PURE_DEATH, false);

		buffs.enable(FLASK_OF_PURE_DEATH);

		assertBuff(FLASK_OF_SUPREME_POWER, false);
		assertBuff(FLASK_OF_PURE_DEATH, true);
	}

	@Test
	void has() {
		assertBuff(FLASK_OF_SUPREME_POWER, false);

		buffs.enable(FLASK_OF_SUPREME_POWER);

		assertBuff(FLASK_OF_SUPREME_POWER, true);

		buffs.disable(FLASK_OF_SUPREME_POWER);

		assertBuff(FLASK_OF_SUPREME_POWER, false);
	}

	Buffs buffs;

	@BeforeEach
	void setup() {
		buffs = getPlayer().getBuffs();
		buffs.reset();
	}

	void assertNoBuffs() {
		assertBuffs(buffs);
	}

	void assertBuff(String name, boolean enabled) {
		assertThat(buffs.has(name)).isEqualTo(enabled);
	}

	void assertBuffs(String... buffNames) {
		assertBuffs(buffs, buffNames);
	}
	
	void assertBuffs(Buffs buffs, String... buffNames) {
		assertThat(buffs.getList().stream().map(Buff::getName)).hasSameElementsAs(List.of(buffNames));
	}
}