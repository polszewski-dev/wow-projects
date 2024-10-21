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
class AccumulatedDurationStatsTest extends WowCharacterSpringTest {
	@Test
	void getDuration() {
		accumulateTestAttributes(AttributeId.DURATION);
		assertThat(durationStats.getDuration()).isEqualTo(160);
	}

	@Test
	void getDurationPct() {
		accumulateTestAttributes(AttributeId.DURATION_PCT);
		assertThat(durationStats.getDurationPct()).isEqualTo(160);
	}

	@Test
	void getHasteRating() {
		accumulateTestAttributes(AttributeId.HASTE_RATING);
		assertThat(durationStats.getHasteRating()).isEqualTo(160);
	}

	@Test
	void getHastePct() {
		accumulateTestAttributes(AttributeId.HASTE_PCT);
		assertThat(durationStats.getHastePct()).isEqualTo(160);
	}

	@Test
	void copy() {
		durationStats.accumulateAttribute(DURATION, 1);
		durationStats.accumulateAttribute(DURATION_PCT, 2);
		durationStats.accumulateAttribute(HASTE_RATING, 3);
		durationStats.accumulateAttribute(HASTE_PCT, 4);

		var copy = durationStats.copy();

		assertThat(copy.getDuration()).isEqualTo(durationStats.getDuration());
		assertThat(copy.getDurationPct()).isEqualTo(durationStats.getDurationPct());
		assertThat(copy.getHasteRating()).isEqualTo(durationStats.getHasteRating());
		assertThat(copy.getHastePct()).isEqualTo(durationStats.getHastePct());
	}

	void accumulateTestAttributes(AttributeId attributeId) {
		var list = List.of(
				Attribute.of(attributeId, 10),
				Attribute.of(attributeId, 20, MiscCondition.PHYSICAL),
				Attribute.of(attributeId, 30, MiscCondition.SPELL),
				Attribute.of(attributeId, 40)
		);

		durationStats.accumulateAttributes(list, 2);
	}

	AccumulatedDurationStats durationStats;

	@BeforeEach
	void setUp() {
		var caster = getCharacter();
		var spell = caster.getAbility(AbilityId.SHADOW_BOLT).orElseThrow();
		var conditionArgs = AttributeConditionArgs.forSpell(caster, spell, null);

		this.durationStats = new AccumulatedDurationStats(conditionArgs);
	}
}