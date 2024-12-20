package wow.character.model.snapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.character.WowCharacterSpringTest;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.spell.AbilityId;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.*;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
class AccumulatedTargetStatsTest extends WowCharacterSpringTest {
	@Test
	void getDamageTaken() {
		accumulateTestAttributes(DAMAGE_TAKEN);
		assertThat(targetStats.getDamageTaken()).isEqualTo(160);
	}

	@Test
	void getDamageTakenPct() {
		accumulateTestAttributes(DAMAGE_TAKEN_PCT);
		assertThat(targetStats.getDamageTakenPct()).isEqualTo(160);
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
	void getReceivedEffectDuration() {
		accumulateTestAttributes(RECEIVED_EFFECT_DURATION);
		assertThat(targetStats.getReceivedEffectDuration()).isEqualTo(160);
	}

	@Test
	void getReceivedEffectDurationPct() {
		accumulateTestAttributes(RECEIVED_EFFECT_DURATION_PCT);
		assertThat(targetStats.getReceivedEffectDurationPct()).isEqualTo(160);
	}

	@Test
	void copy() {
		targetStats.accumulateAttribute(DAMAGE_TAKEN, 1);
		targetStats.accumulateAttribute(DAMAGE_TAKEN_PCT, 1);
		targetStats.accumulateAttribute(POWER_TAKEN, 1);
		targetStats.accumulateAttribute(CRIT_TAKEN_PCT, 1);
		targetStats.accumulateAttribute(RECEIVED_EFFECT_DURATION, 1);
		targetStats.accumulateAttribute(RECEIVED_EFFECT_DURATION_PCT, 1);

		var copy = targetStats.copy();

		assertThat(copy.getDamageTaken()).isEqualTo(targetStats.getDamageTaken());
		assertThat(copy.getDamageTakenPct()).isEqualTo(targetStats.getDamageTakenPct());
		assertThat(copy.getPowerTaken()).isEqualTo(targetStats.getPowerTaken());
		assertThat(copy.getCritTakenPct()).isEqualTo(targetStats.getCritTakenPct());
		assertThat(copy.getReceivedEffectDuration()).isEqualTo(targetStats.getReceivedEffectDuration());
		assertThat(copy.getReceivedEffectDurationPct()).isEqualTo(targetStats.getReceivedEffectDurationPct());
	}

	void accumulateTestAttributes(AttributeId attributeId) {
		var list = List.of(
				Attribute.of(attributeId, 10),
				Attribute.of(attributeId, 20, MiscCondition.PHYSICAL),
				Attribute.of(attributeId, 30, MiscCondition.SPELL),
				Attribute.of(attributeId, 40)
		);

		targetStats.accumulateAttributes(list, 2);
	}

	AccumulatedTargetStats targetStats;

	@BeforeEach
	void setUp() {
		var caster = getCharacter();
		var spell = caster.getAbility(AbilityId.SHADOW_BOLT).orElseThrow();
		var conditionArgs = AttributeConditionArgs.forSpell(caster, spell, null);

		this.targetStats = new AccumulatedTargetStats(conditionArgs);
	}
}