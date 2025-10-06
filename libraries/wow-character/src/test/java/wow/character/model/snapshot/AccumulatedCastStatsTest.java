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
import static wow.test.commons.AbilityNames.SHADOW_BOLT;

/**
 * User: POlszewski
 * Date: 2023-10-21
 */
class AccumulatedCastStatsTest extends WowCharacterSpringTest {
	@Test
	void getHasteRating() {
		accumulateTestAttributes(HASTE_RATING);
		assertThat(castStats.getHasteRating()).isEqualTo(160);
	}

	@Test
	void getHastePct() {
		accumulateTestAttributes(HASTE_PCT);
		assertThat(castStats.getHastePct()).isEqualTo(160);
	}

	@Test
	void getCastTime() {
		accumulateTestAttributes(CAST_TIME);
		assertThat(castStats.getCastTime()).isEqualTo(160);
	}

	@Test
	void getCastTimePct() {
		accumulateTestAttributes(CAST_TIME_PCT);
		assertThat(castStats.getCastTimePct()).isEqualTo(160);
	}

	@Test
	void copy() {
		castStats.accumulateAttribute(HASTE_RATING, 1);
		castStats.accumulateAttribute(HASTE_PCT, 2);
		castStats.accumulateAttribute(CAST_TIME, 3);
		castStats.accumulateAttribute(CAST_TIME_PCT, 4);

		var copy = castStats.copy();

		assertThat(copy.getHasteRating()).isEqualTo(castStats.getHasteRating());
		assertThat(copy.getHastePct()).isEqualTo(castStats.getHastePct());
		assertThat(copy.getCastTime()).isEqualTo(castStats.getCastTime());
		assertThat(copy.getCastTimePct()).isEqualTo(castStats.getCastTimePct());
	}

	void accumulateTestAttributes(AttributeId attributeId) {
		var list = List.of(
				Attribute.of(attributeId, 10),
				Attribute.of(attributeId, 20, PHYSICAL),
				Attribute.of(attributeId, 30, SPELL),
				Attribute.of(attributeId, 40)
		);

		castStats.accumulateAttributes(list, 2);
	}

	AccumulatedCastStats castStats;

	@BeforeEach
	void setUp() {
		var caster = getCharacter();
		var spell = caster.getAbility(SHADOW_BOLT).orElseThrow();
		var conditionArgs = AttributeConditionArgs.forSpell(caster, spell, null);

		this.castStats = new AccumulatedCastStats(conditionArgs);
	}
}