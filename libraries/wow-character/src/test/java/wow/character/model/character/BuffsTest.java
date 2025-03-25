package wow.character.model.character;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import wow.character.WowCharacterSpringTest;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.buff.Buff;
import wow.commons.model.buff.BuffIdAndRank;
import wow.commons.model.pve.PhaseId;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static wow.character.model.character.BuffListType.CHARACTER_BUFF;
import static wow.commons.model.attribute.AttributeId.HEALTH_GENERATED_PCT;
import static wow.commons.model.attribute.AttributeId.POWER;
import static wow.commons.model.attribute.condition.MiscCondition.SPELL_DAMAGE;
import static wow.commons.model.buff.BuffId.*;
import static wow.commons.model.talent.TalentId.DEMONIC_AEGIS;

/**
 * User: POlszewski
 * Date: 2023-08-03
 */
class BuffsTest extends WowCharacterSpringTest {
	@Test
	void copy() {
		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertThat(buffs.getList()).hasSize(1);
		assertThat(buffs.getList().get(0).getId()).isEqualTo(new BuffIdAndRank(ARCANE_BRILLIANCE, 2));

		Buffs copy = buffs.copy();
		buffs.reset();

		assertThat(buffs.getList()).isEmpty();

		assertThat(copy.getList()).hasSize(1);
		assertThat(copy.getList().get(0).getId()).isEqualTo(new BuffIdAndRank(ARCANE_BRILLIANCE, 2));
	}

	@Test
	void getList() {
		assertThat(buffs.getList()).isEmpty();

		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertThat(buffs.getList()).hasSize(1);
		assertThat(buffs.getList().get(0).getId()).isEqualTo(new BuffIdAndRank(ARCANE_BRILLIANCE, 2));

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
		assertThat(buffs.has(ARCANE_BRILLIANCE)).isFalse();

		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertThat(buffs.has(ARCANE_BRILLIANCE)).isTrue();

		buffs.reset();

		assertThat(buffs.has(ARCANE_BRILLIANCE)).isFalse();

		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertThat(buffs.has(ARCANE_BRILLIANCE)).isTrue();
	}

	@Test
	void setHighestRanks() {
		assertThat(buffs.has(ARCANE_BRILLIANCE)).isFalse();
		assertThat(buffs.has(FLASK_OF_SUPREME_POWER)).isFalse();

		buffs.setHighestRanks(List.of(ARCANE_BRILLIANCE, FLASK_OF_SUPREME_POWER));

		assertThat(buffs.has(ARCANE_BRILLIANCE)).isTrue();
		assertThat(buffs.has(FLASK_OF_SUPREME_POWER)).isTrue();

		assertThat(buffs.getList()).hasSize(2);
		assertThat(buffs.getList().get(0).getId()).isEqualTo(new BuffIdAndRank(ARCANE_BRILLIANCE, 2));
		assertThat(buffs.getList().get(1).getId()).isEqualTo(new BuffIdAndRank(FLASK_OF_SUPREME_POWER, 0));

		assertThatNoException().isThrownBy(() -> buffs.setHighestRanks(List.of(WARCHIEFS_BLESSING, FLASK_OF_PURE_DEATH)));

		assertThat(buffs.getList()).hasSize(1);
		assertThat(buffs.getList().get(0).getId()).isEqualTo(new BuffIdAndRank(FLASK_OF_PURE_DEATH, 0));
	}

	@Test
	void set() {
		assertThat(buffs.has(ARCANE_BRILLIANCE)).isFalse();

		buffs.set(List.of(new BuffIdAndRank(ARCANE_BRILLIANCE, 2)));

		assertThat(buffs.has(ARCANE_BRILLIANCE)).isTrue();

		assertThat(buffs.getList()).hasSize(1);
		assertThat(buffs.getList().get(0).getId()).isEqualTo(new BuffIdAndRank(ARCANE_BRILLIANCE, 2));

		var invalidIds = List.of(new BuffIdAndRank(ARCANE_BRILLIANCE, 10));

		assertThatThrownBy(() -> buffs.set(invalidIds)).isInstanceOf(IllegalArgumentException.class);
		assertThat(buffs.getList()).isEmpty();
	}

	@Test
	void enable() {
		assertThat(buffs.has(ARCANE_BRILLIANCE)).isFalse();

		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertThat(buffs.getList()).hasSize(1);
		assertThat(buffs.getList().get(0).getId()).isEqualTo(new BuffIdAndRank(ARCANE_BRILLIANCE, 2));

		buffs.enable(ARCANE_BRILLIANCE, 1);

		assertThat(buffs.getList()).hasSize(1);
		assertThat(buffs.getList().get(0).getId()).isEqualTo(new BuffIdAndRank(ARCANE_BRILLIANCE, 1));

		assertThatThrownBy(() -> buffs.enable(ARCANE_BRILLIANCE, 10)).isInstanceOf(IllegalArgumentException.class);

		assertThat(buffs.getList()).hasSize(1);
		assertThat(buffs.getList().get(0).getId()).isEqualTo(new BuffIdAndRank(ARCANE_BRILLIANCE, 1));
	}

	@Test
	void exclusionGroups() {
		assertThat(buffs.has(DEMON_ARMOR)).isFalse();
		assertThat(buffs.has(FEL_ARMOR)).isFalse();

		buffs.enable(DEMON_ARMOR, 6);

		assertThat(buffs.has(DEMON_ARMOR)).isTrue();
		assertThat(buffs.has(FEL_ARMOR)).isFalse();

		buffs.enable(FEL_ARMOR, 2);

		assertThat(buffs.has(DEMON_ARMOR)).isFalse();
		assertThat(buffs.has(FEL_ARMOR)).isTrue();
	}

	@Test
	void has() {
		assertThat(buffs.has(ARCANE_BRILLIANCE)).isFalse();

		buffs.enable(ARCANE_BRILLIANCE, 2);

		assertThat(buffs.has(ARCANE_BRILLIANCE)).isTrue();

		buffs.disable(ARCANE_BRILLIANCE);

		assertThat(buffs.has(ARCANE_BRILLIANCE)).isFalse();
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
				.filter(x -> x.getBuffId() == FEL_ARMOR)
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
		List<Buff> availableBuffs = buffRepository.getAvailableBuffs(PhaseId.TBC_P5).stream()
				.filter(x -> x.isAvailableTo(getCharacter()))
				.filter(CHARACTER_BUFF.getFilter())
				.toList();

		buffs = new Buffs(CHARACTER_BUFF);
		buffs.setAvailable(availableBuffs);
	}
}