package wow.character.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffNameRank;
import wow.commons.model.pve.PhaseId;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static wow.character.model.character.BuffListType.CHARACTER_BUFF;
import static wow.commons.model.attribute.AttributeId.HEALTH_GENERATED_PCT;
import static wow.commons.model.attribute.AttributeId.POWER;
import static wow.commons.model.attribute.condition.MiscCondition.SPELL_DAMAGE;
import static wow.commons.model.talent.TalentId.DEMONIC_AEGIS;
import static wow.test.commons.BuffNames.*;

/**
 * User: POlszewski
 * Date: 2023-08-03
 */
class BuffsTest extends WowCharacterSpringTest {
	@Test
	void copy() {
		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertThat(buffs.getList()).hasSize(1);
		assertBuff(buffs.getList().getFirst(), ARCANE_BRILLIANCE, 2);

		var copy = buffs.copy();
		buffs.reset();

		assertThat(buffs.getList()).isEmpty();

		assertThat(copy.getList()).hasSize(1);
		assertBuff(copy.getList().getFirst(), ARCANE_BRILLIANCE, 2);
	}

	@Test
	void getList() {
		assertThat(buffs.getList()).isEmpty();

		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertThat(buffs.getList()).hasSize(1);
		assertBuff(buffs.getList().getFirst(), ARCANE_BRILLIANCE, 2);

		buffs.disable(ARCANE_BRILLIANCE);

		assertThat(buffs.getList()).isEmpty();
	}

	@Test
	void getAvailableHighestRanks() {
		var names = buffs.getAvailableHighestRanks().stream()
				.map(x -> x.getName() + "#" + x.getRank())
				.toList();

		assertThat(names).hasSameElementsAs(List.of(
				"Arcane Brilliance#2",
				"Prayer of Fortitude#3",
				"Prayer of Spirit#2",
				"Gift of the Wild#3",
				"Greater Blessing of Kings#0",
				"Demon Armor#6",
				"Fel Armor#2",
				"Touch of Shadow#0",
				"Burning Wish#0",
				"Brilliant Wizard Oil#0",
				"Superior Wizard Oil#0",
				"Well Fed (sp)#0",
				"Flask of Supreme Power#0",
				"Flask of Pure Death#0",
				"Moonkin Aura#0",
				"Wrath of Air Totem#1",
				"Totem of Wrath#1",
				"Drums of Battle#0"
		));
	}

	@Test
	void reset() {
		assertBuff(ARCANE_BRILLIANCE, false);

		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertBuff(ARCANE_BRILLIANCE, true);

		buffs.reset();

		assertBuff(ARCANE_BRILLIANCE, false);

		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertBuff(ARCANE_BRILLIANCE, true);
	}

	@Test
	void setHighestRanks() {
		assertBuff(ARCANE_BRILLIANCE, false);
		assertBuff(FLASK_OF_SUPREME_POWER, false);

		buffs.setHighestRanks(List.of(ARCANE_BRILLIANCE, FLASK_OF_SUPREME_POWER));

		assertBuff(ARCANE_BRILLIANCE, true);
		assertBuff(FLASK_OF_SUPREME_POWER, true);

		assertThat(buffs.getList()).hasSize(2);
		assertBuff(buffs.getList().getFirst(), ARCANE_BRILLIANCE, 2);
		assertBuff(buffs.getList().get(1), FLASK_OF_SUPREME_POWER, 0);

		assertThatNoException().isThrownBy(() -> buffs.setHighestRanks(List.of(WARCHIEFS_BLESSING, FLASK_OF_PURE_DEATH)));

		assertThat(buffs.getList()).hasSize(1);
		assertBuff(buffs.getList().getFirst(), FLASK_OF_PURE_DEATH, 0);
	}

	@Test
	void set() {
		assertBuff(ARCANE_BRILLIANCE, false);

		buffs.set(List.of(new BuffNameRank(ARCANE_BRILLIANCE, 2)));

		assertBuff(ARCANE_BRILLIANCE, true);

		assertThat(buffs.getList()).hasSize(1);
		assertBuff(buffs.getList().getFirst(), ARCANE_BRILLIANCE, 2);

		var invalidIds = List.of(new BuffNameRank(ARCANE_BRILLIANCE, 10));

		assertThatThrownBy(() -> buffs.set(invalidIds)).isInstanceOf(NoSuchElementException.class);
		assertThat(buffs.getList()).isEmpty();
	}

	@Test
	void enable() {
		assertBuff(ARCANE_BRILLIANCE, false);

		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertThat(buffs.getList()).hasSize(1);
		assertBuff(buffs.getList().getFirst(), ARCANE_BRILLIANCE, 2);

		buffs.enable(ARCANE_BRILLIANCE, 1);

		assertThat(buffs.getList()).hasSize(1);
		assertBuff(buffs.getList().getFirst(), ARCANE_BRILLIANCE, 1);

		assertThatThrownBy(() -> buffs.enable(ARCANE_BRILLIANCE, 10)).isInstanceOf(NoSuchElementException.class);

		assertThat(buffs.getList()).hasSize(1);
		assertBuff(buffs.getList().getFirst(), ARCANE_BRILLIANCE, 1);
	}

	@Test
	void exclusionGroups() {
		assertBuff(DEMON_ARMOR, false);
		assertBuff(FEL_ARMOR, false);

		buffs.enable(DEMON_ARMOR, 6);

		assertBuff(DEMON_ARMOR, true);
		assertBuff(FEL_ARMOR, false);

		buffs.enable(FEL_ARMOR, 2);

		assertBuff(DEMON_ARMOR, false);
		assertBuff(FEL_ARMOR, true);
	}

	@Test
	void has() {
		assertBuff(ARCANE_BRILLIANCE, false);

		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertBuff(ARCANE_BRILLIANCE, true);

		buffs.disable(ARCANE_BRILLIANCE);

		assertBuff(ARCANE_BRILLIANCE, false);
	}

	@ParameterizedTest
	@CsvSource({
			"0, 20, 100",
			"1, 22, 110",
			"2, 24, 120",
			"3, 26, 130",
	})
	void demonicAegisCorrectlyAffectsFelArmor(int rank, int healthGeneratedPct, int spellDamage) {
		var player = getCharacter();

		player.resetEquipment();
		player.resetBuffs();
		player.resetBuild();

		if (rank > 0) {
			player.getTalents().enableTalent(DEMONIC_AEGIS, rank);
		}

		characterService.updateAfterRestrictionChange(player);

		player.getBuffs().enable(FEL_ARMOR, 2, true);

		var buff = player.getBuffs().getList().stream()
				.filter(x -> x.getName().equals(FEL_ARMOR))
				.findFirst()
				.orElseThrow();

		assertThat(buff.getEffect().getModifierAttributeList()).isEqualTo(List.of(
				Attribute.of(HEALTH_GENERATED_PCT, healthGeneratedPct),
				Attribute.of(POWER, spellDamage, SPELL_DAMAGE)
		));
	}

	Buffs buffs;

	@BeforeEach
	void setup() {
		var availableBuffs = buffRepository.getAvailableBuffs(PhaseId.TBC_P5).stream()
				.filter(x -> x.isAvailableTo(getCharacter()))
				.filter(CHARACTER_BUFF.getFilter())
				.toList();

		buffs = new Buffs(CHARACTER_BUFF);
		buffs.setAvailable(availableBuffs);
	}

	void assertBuff(Buff buff, String name, int rank) {
		assertThat(buff.getNameRank()).isEqualTo(new BuffNameRank(name, rank));
	}

	void assertBuff(String name, boolean enabled) {
		assertThat(buffs.has(name)).isEqualTo(enabled);
	}
}