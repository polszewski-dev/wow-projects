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
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
class AccumulatedTargetStatsTest extends WowCharacterSpringTest {
	@Test
	void getDamageTaken() {
		accumulateTestAttributes(DAMAGE_TAKEN);
		assertThat(targetStats.getAmountTaken()).isEqualTo(160);
	}

	@Test
	void getDamageTakenPct() {
		accumulateTestAttributes(DAMAGE_TAKEN_PCT);
		assertThat(targetStats.getAmountTakenPct()).isEqualTo(160);
	}

	@Test
	void getHealingTaken() {
		accumulateTestAttributes(HEALING_TAKEN);
		assertThat(targetStats.getAmountTakenPct()).isZero();
	}

	@Test
	void getHealingTakenPct() {
		accumulateTestAttributes(HEALING_TAKEN_PCT);
		assertThat(targetStats.getAmountTakenPct()).isZero();
	}

	@Test
	void getPowerTaken() {
		accumulateTestAttributes(POWER_TAKEN);
		assertThat(targetStats.getPowerTaken()).isEqualTo(160);
	}

	@Test
	void getCritTakenPct() {
		accumulateTestAttributes(CRIT_TAKEN_PCT);
		assertThat(targetStats.getCritTakenPct()).isEqualTo(160);
	}

	@Test
	void copy() {
		targetStats.accumulateAttribute(DAMAGE_TAKEN, 1);
		targetStats.accumulateAttribute(DAMAGE_TAKEN_PCT, 2);
		targetStats.accumulateAttribute(HEALING_TAKEN, 3);
		targetStats.accumulateAttribute(HEALING_TAKEN_PCT, 4);
		targetStats.accumulateAttribute(POWER_TAKEN, 5);
		targetStats.accumulateAttribute(CRIT_TAKEN_PCT, 6);
		targetStats.accumulateAttribute(RECEIVED_EFFECT_DURATION, 7);
		targetStats.accumulateAttribute(RECEIVED_EFFECT_DURATION_PCT, 8);

		var copy = targetStats.copy();

		assertThat(copy.getAmountTaken()).isEqualTo(targetStats.getAmountTaken());
		assertThat(copy.getAmountTakenPct()).isEqualTo(targetStats.getAmountTakenPct());
		assertThat(copy.getPowerTaken()).isEqualTo(targetStats.getPowerTaken());
		assertThat(copy.getCritTakenPct()).isEqualTo(targetStats.getCritTakenPct());
	}

	void accumulateTestAttributes(AttributeId attributeId) {
		var list = List.of(
				Attribute.of(attributeId, 10),
				Attribute.of(attributeId, 20, PHYSICAL),
				Attribute.of(attributeId, 30, SPELL),
				Attribute.of(attributeId, 40)
		);

		targetStats.accumulateAttributes(list, 2);
	}

	AccumulatedTargetStats targetStats;

	@BeforeEach
	void setUp() {
		var caster = getCharacter();
		var spell = caster.getAbility(SHADOW_BOLT).orElseThrow();
		var conditionArgs = AttributeConditionArgs.forSpell(caster, spell, null, SPELL_DAMAGE, null);

		this.targetStats = new AccumulatedTargetStats(conditionArgs);
	}
}