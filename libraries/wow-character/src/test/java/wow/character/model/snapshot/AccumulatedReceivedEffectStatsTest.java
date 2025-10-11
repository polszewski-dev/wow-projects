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
import static wow.commons.model.attribute.AttributeId.RECEIVED_EFFECT_DURATION;
import static wow.commons.model.attribute.AttributeId.RECEIVED_EFFECT_DURATION_PCT;
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
class AccumulatedReceivedEffectStatsTest extends WowCharacterSpringTest {
	@Test
	void getReceivedEffectDuration() {
		accumulateTestAttributes(RECEIVED_EFFECT_DURATION);
		assertThat(receivedEffectStats.getReceivedEffectDuration()).isEqualTo(160);
	}

	@Test
	void getReceivedEffectDurationPct() {
		accumulateTestAttributes(RECEIVED_EFFECT_DURATION_PCT);
		assertThat(receivedEffectStats.getReceivedEffectDurationPct()).isEqualTo(160);
	}

	@Test
	void copy() {
		receivedEffectStats.accumulateAttribute(RECEIVED_EFFECT_DURATION, 1);
		receivedEffectStats.accumulateAttribute(RECEIVED_EFFECT_DURATION_PCT, 2);

		var copy = receivedEffectStats.copy();

		assertThat(copy.getReceivedEffectDuration()).isEqualTo(receivedEffectStats.getReceivedEffectDuration());
		assertThat(copy.getReceivedEffectDurationPct()).isEqualTo(receivedEffectStats.getReceivedEffectDurationPct());
	}

	void accumulateTestAttributes(AttributeId attributeId) {
		var list = List.of(
				Attribute.of(attributeId, 10),
				Attribute.of(attributeId, 20, PHYSICAL),
				Attribute.of(attributeId, 30, SPELL),
				Attribute.of(attributeId, 40)
		);

		receivedEffectStats.accumulateAttributes(list, 2);
	}

	AccumulatedReceivedEffectStats receivedEffectStats;

	@BeforeEach
	void setUp() {
		var caster = getCharacter();
		var spell = caster.getAbility(SHADOW_BOLT).orElseThrow();
		var conditionArgs = AttributeConditionArgs.forSpell(caster, spell, null);

		this.receivedEffectStats = new AccumulatedReceivedEffectStats(conditionArgs);
	}
}