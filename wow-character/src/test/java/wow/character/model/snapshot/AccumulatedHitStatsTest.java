package wow.character.model.snapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.condition.AttributeConditionArgs;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.spell.ActionType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.AttributeId.HIT_PCT;
import static wow.commons.model.attribute.AttributeId.HIT_RATING;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
class AccumulatedHitStatsTest {
	@Test
	void getHitRating() {
		accumulateTestAttributes(HIT_RATING);
		assertThat(hitStats.getHitRating()).isEqualTo(160);
	}

	@Test
	void getHitPct() {
		accumulateTestAttributes(HIT_PCT);
		assertThat(hitStats.getHitPct()).isEqualTo(160);
	}

	@Test
	void copy() {
		hitStats.accumulateAttribute(HIT_RATING, 1);
		hitStats.accumulateAttribute(HIT_PCT, 2);

		var copy = hitStats.copy();

		assertThat(copy.getHitRating()).isEqualTo(hitStats.getHitRating());
		assertThat(copy.getHitPct()).isEqualTo(hitStats.getHitPct());
	}

	void accumulateTestAttributes(AttributeId attributeId) {
		var list = List.of(
				Attribute.of(attributeId, 10),
				Attribute.of(attributeId, 20, MiscCondition.PHYSICAL),
				Attribute.of(attributeId, 30, MiscCondition.SPELL),
				Attribute.of(attributeId, 40)
		);

		hitStats.accumulateAttributes(list, 2);
	}

	int level = 70;
	AccumulatedHitStats hitStats;

	@BeforeEach
	void setUp() {
		var conditionArgs = new AttributeConditionArgs(ActionType.SPELL);
		this.hitStats = new AccumulatedHitStats(conditionArgs, level);
	}
}