package wow.character.model.snapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.character.constant.AttributeConditions.PHYSICAL;
import static wow.character.constant.AttributeConditions.SPELL;
import static wow.commons.model.attribute.AttributeId.*;

/**
 * User: POlszewski
 * Date: 2025-10-12
 */
class AccumulatedRegenStatsTest extends WowCharacterSpringTest {
	@Test
	void getHp5() {
		accumulateTestAttributes(HP5);
		assertThat(regenStats.getHp5()).isEqualTo(100);
	}

	@Test
	void getMp5() {
		accumulateTestAttributes(MP5);
		assertThat(regenStats.getMp5()).isEqualTo(100);
	}

	@Test
	void getHealthGeneratedPct() {
		accumulateTestAttributes(HEALTH_GENERATED_PCT);
		assertThat(regenStats.getHealthGeneratedPct()).isEqualTo(100);
	}

	@Test
	void getHealthRegenPct() {
		accumulateTestAttributes(HEALTH_REGEN_PCT);
		assertThat(regenStats.getHealthRegenPct()).isEqualTo(100);
	}

	@Test
	void getManaRegenPct() {
		accumulateTestAttributes(MANA_REGEN_PCT);
		assertThat(regenStats.getManaRegenPct()).isEqualTo(100);
	}

	@Test
	void getInCombatHealthRegenPct() {
		accumulateTestAttributes(IN_COMBAT_HEALTH_REGEN_PCT);
		assertThat(regenStats.getInCombatHealthRegenPct()).isEqualTo(100);
	}

	@Test
	void getInCombatManaRegenPct() {
		accumulateTestAttributes(IN_COMBAT_MANA_REGEN_PCT);
		assertThat(regenStats.getInCombatManaRegenPct()).isEqualTo(100);
	}

	@Test
	void copy() {
		regenStats.accumulateAttribute(HP5, 1);
		regenStats.accumulateAttribute(MP5, 2);
		regenStats.accumulateAttribute(HEALTH_GENERATED_PCT, 3);
		regenStats.accumulateAttribute(HEALTH_REGEN_PCT, 4);
		regenStats.accumulateAttribute(MANA_REGEN_PCT, 5);
		regenStats.accumulateAttribute(IN_COMBAT_HEALTH_REGEN_PCT, 6);
		regenStats.accumulateAttribute(IN_COMBAT_MANA_REGEN_PCT, 7);

		var copy = regenStats.copy();

		assertThat(copy.getHp5()).isEqualTo(regenStats.getHp5());
		assertThat(copy.getMp5()).isEqualTo(regenStats.getMp5());
		assertThat(copy.getHealthGeneratedPct()).isEqualTo(regenStats.getHealthGeneratedPct());
		assertThat(copy.getHealthRegenPct()).isEqualTo(regenStats.getHealthRegenPct());
		assertThat(copy.getManaRegenPct()).isEqualTo(regenStats.getManaRegenPct());
		assertThat(copy.getInCombatHealthRegenPct()).isEqualTo(regenStats.getInCombatHealthRegenPct());
		assertThat(copy.getInCombatManaRegenPct()).isEqualTo(regenStats.getInCombatManaRegenPct());
	}

	void accumulateTestAttributes(AttributeId attributeId) {
		var list = List.of(
				Attribute.of(attributeId, 10),
				Attribute.of(attributeId, 20, PHYSICAL),
				Attribute.of(attributeId, 30, SPELL),
				Attribute.of(attributeId, 40)
		);

		regenStats.accumulateAttributes(list, 2);
	}

	AccumulatedRegenStats regenStats;

	@BeforeEach
	void setUp() {
		var caster = getCharacter();
		var conditionArgs = AttributeConditionArgs.forRegen(caster);

		this.regenStats = new AccumulatedRegenStats(conditionArgs);
	}
}