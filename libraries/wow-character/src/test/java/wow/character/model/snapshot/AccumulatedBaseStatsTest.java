package wow.character.model.snapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.model.character.BaseStatInfo;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.constant.AttributeConditions.PHYSICAL;
import static wow.character.model.snapshot.AttributePredicates.OWNER_OR_AURAS;
import static wow.commons.model.attribute.AttributeId.*;

/**
 * User: POlszewski
 * Date: 2023-10-16
 */
class AccumulatedBaseStatsTest extends WowCharacterSpringTest {
	@Test
	void accumulateBaseStatInfo() {
		var baseStatInfo = getBaseStatInfo();

		assertThat(baseStats.getBaseStrength()).isEqualTo(baseStatInfo.getBaseStrength());
		assertThat(baseStats.getBaseAgility()).isEqualTo(baseStatInfo.getBaseAgility());
		assertThat(baseStats.getBaseStamina()).isEqualTo(baseStatInfo.getBaseStamina());
		assertThat(baseStats.getBaseIntellect()).isEqualTo(baseStatInfo.getBaseIntellect());
		assertThat(baseStats.getBaseSpirit()).isEqualTo(baseStatInfo.getBaseSpirit());

		assertThat(baseStats.getMaxHealth()).isEqualTo(baseStatInfo.getBaseHealth());
		assertThat(baseStats.getMaxMana()).isEqualTo(baseStatInfo.getBaseMana());
	}

	@Test
	void getStrength() {
		accumulateTestAttributes(STRENGTH);
		assertThat(baseStats.getStrength()).isEqualTo(80);
	}

	@Test
	void getStrengthPct() {
		accumulateTestAttributes(STRENGTH_PCT);
		assertThat(baseStats.getStrengthPct()).isEqualTo(80);
	}

	@Test
	void getAgility() {
		accumulateTestAttributes(AGILITY);
		assertThat(baseStats.getAgility()).isEqualTo(80);
	}

	@Test
	void getAgilityPct() {
		accumulateTestAttributes(AGILITY_PCT);
		assertThat(baseStats.getAgilityPct()).isEqualTo(80);
	}

	@Test
	void getStamina() {
		accumulateTestAttributes(STAMINA);
		assertThat(baseStats.getStamina()).isEqualTo(80);
	}

	@Test
	void getStaminaPct() {
		accumulateTestAttributes(STAMINA_PCT);
		assertThat(baseStats.getStaminaPct()).isEqualTo(80);
	}

	@Test
	void getIntellect() {
		accumulateTestAttributes(INTELLECT);
		assertThat(baseStats.getIntellect()).isEqualTo(80);
	}

	@Test
	void getIntellectPct() {
		accumulateTestAttributes(INTELLECT_PCT);
		assertThat(baseStats.getIntellectPct()).isEqualTo(80);
	}

	@Test
	void getSpirit() {
		accumulateTestAttributes(SPIRIT);
		assertThat(baseStats.getSpirit()).isEqualTo(80);
	}

	@Test
	void getSpiritPct() {
		accumulateTestAttributes(SPIRIT_PCT);
		assertThat(baseStats.getSpiritPct()).isEqualTo(80);
	}

	@Test
	void getBaseStats() {
		accumulateTestAttributes(BASE_STATS);

		var baseStatInfo = getBaseStatInfo();

		assertThat(baseStats.getBaseStrength()).isEqualTo(80 + baseStatInfo.getBaseStrength());
		assertThat(baseStats.getBaseAgility()).isEqualTo(80 + baseStatInfo.getBaseAgility());
		assertThat(baseStats.getBaseStamina()).isEqualTo(80 + baseStatInfo.getBaseStamina());
		assertThat(baseStats.getBaseIntellect()).isEqualTo(80 + baseStatInfo.getBaseIntellect());
		assertThat(baseStats.getBaseSpirit()).isEqualTo(80 + baseStatInfo.getBaseSpirit());
	}

	@Test
	void getBaseStatsPct() {
		accumulateTestAttributes(BASE_STATS_PCT);
		assertThat(baseStats.getBaseStatsPct()).isEqualTo(80);
	}

	@Test
	void getStats() {
		accumulateTestAttributes(STATS);
		assertThat(baseStats.getStrength()).isEqualTo(80);
		assertThat(baseStats.getAgility()).isEqualTo(80);
		assertThat(baseStats.getStamina()).isEqualTo(80);
		assertThat(baseStats.getIntellect()).isEqualTo(80);
		assertThat(baseStats.getSpirit()).isEqualTo(80);
	}

	@Test
	void getStatsPct() {
		accumulateTestAttributes(STATS_PCT);
		assertThat(baseStats.getStrengthPct()).isEqualTo(80);
		assertThat(baseStats.getAgilityPct()).isEqualTo(80);
		assertThat(baseStats.getStaminaPct()).isEqualTo(80);
		assertThat(baseStats.getIntellectPct()).isEqualTo(80);
		assertThat(baseStats.getSpiritPct()).isEqualTo(80);
	}

	@Test
	void getMaxHealth() {
		accumulateTestAttributes(MAX_HEALTH);
		assertThat(baseStats.getMaxHealth()).isEqualTo(80 + getBaseStatInfo().getBaseHealth());
	}

	@Test
	void getMaxHealthPct() {
		accumulateTestAttributes(MAX_HEALTH_PCT);
		assertThat(baseStats.getMaxHealthPct()).isEqualTo(80);
	}

	@Test
	void getMaxMana() {
		accumulateTestAttributes(MAX_MANA);
		assertThat(baseStats.getMaxMana()).isEqualTo(80 + getBaseStatInfo().getBaseMana());
	}

	@Test
	void getMaxManaPct() {
		accumulateTestAttributes(MAX_MANA_PCT);
		assertThat(baseStats.getMaxManaPct()).isEqualTo(80);
	}

	@Test
	void copy() {
		baseStats.accumulateAttribute(STRENGTH, 1);
		baseStats.accumulateAttribute(STRENGTH_PCT, 2);
		baseStats.accumulateAttribute(AGILITY, 3);
		baseStats.accumulateAttribute(AGILITY_PCT, 4);
		baseStats.accumulateAttribute(INTELLECT, 5);
		baseStats.accumulateAttribute(INTELLECT_PCT, 6);
		baseStats.accumulateAttribute(STAMINA, 7);
		baseStats.accumulateAttribute(STAMINA_PCT, 8);
		baseStats.accumulateAttribute(SPIRIT, 9);
		baseStats.accumulateAttribute(SPIRIT_PCT, 10);
		baseStats.accumulateAttribute(BASE_STATS, 11);
		baseStats.accumulateAttribute(BASE_STATS_PCT, 12);
		baseStats.accumulateAttribute(MAX_HEALTH, 13);
		baseStats.accumulateAttribute(MAX_HEALTH_PCT, 14);
		baseStats.accumulateAttribute(MAX_MANA, 15);
		baseStats.accumulateAttribute(MAX_MANA_PCT, 16);

		var copy = baseStats.copy();

		assertThat(copy.getStrength()).isEqualTo(baseStats.getStrength());
		assertThat(copy.getStrengthPct()).isEqualTo(baseStats.getStrengthPct());
		assertThat(copy.getAgility()).isEqualTo(baseStats.getAgility());
		assertThat(copy.getAgilityPct()).isEqualTo(baseStats.getAgilityPct());
		assertThat(copy.getStamina()).isEqualTo(baseStats.getStamina());
		assertThat(copy.getStaminaPct()).isEqualTo(baseStats.getStaminaPct());
		assertThat(copy.getIntellect()).isEqualTo(baseStats.getIntellect());
		assertThat(copy.getIntellectPct()).isEqualTo(baseStats.getIntellectPct());
		assertThat(copy.getSpirit()).isEqualTo(baseStats.getSpirit());
		assertThat(copy.getSpiritPct()).isEqualTo(baseStats.getSpiritPct());
		assertThat(copy.getBaseStatsPct()).isEqualTo(baseStats.getBaseStatsPct());
		assertThat(copy.getMaxHealth()).isEqualTo(baseStats.getMaxHealth());
		assertThat(copy.getMaxHealthPct()).isEqualTo(baseStats.getMaxHealthPct());
		assertThat(copy.getMaxMana()).isEqualTo(baseStats.getMaxMana());
		assertThat(copy.getMaxManaPct()).isEqualTo(baseStats.getMaxManaPct());
	}

	void accumulateTestAttributes(AttributeId attributeId) {
		var list = List.of(
				Attribute.of(attributeId, 10),
				Attribute.of(attributeId, 20, PHYSICAL),
				Attribute.of(attributeId, 30)
		);

		baseStats.accumulateAttributes(list, 2, OWNER_OR_AURAS);
	}

	AccumulatedBaseStats baseStats;

	@BeforeEach
	void setUp() {
		var caster = getPlayer();
		var conditionArgs = AttributeConditionArgs.forBaseStats(caster);

		this.baseStats = new AccumulatedBaseStats(conditionArgs);
	}

	private BaseStatInfo getBaseStatInfo() {
		return baseStats.conditionArgs.getCaster().getBaseStatInfo();
	}
}