package wow.character.model.snapshot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.condition.AttributeConditionArgs;
import wow.commons.model.attribute.condition.MiscCondition;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.spell.ActionType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static wow.commons.model.attribute.primitive.PrimitiveAttributeId.*;

/**
 * User: POlszewski
 * Date: 2023-10-21
 */
class AccumulatedCostStatsTest {
	@Test
	void getManaCost() {
		accumulateTestAttributes(MANA_COST);
		assertThat(costStats.getManaCost()).isEqualTo(160);
	}

	@Test
	void getManaCostPct() {
		accumulateTestAttributes(MANA_COST_PCT);
		assertThat(costStats.getManaCostPct()).isEqualTo(160);
	}

	@Test
	void getEnergyCost() {
		accumulateTestAttributes(ENERGY_COST);
		assertThat(costStats.getEnergyCost()).isEqualTo(160);
	}

	@Test
	void getEnergyCostPct() {
		accumulateTestAttributes(ENERGY_COST_PCT);
		assertThat(costStats.getEnergyCostPct()).isEqualTo(160);
	}

	@Test
	void getRageCost() {
		accumulateTestAttributes(RAGE_COST);
		assertThat(costStats.getRageCost()).isEqualTo(160);
	}

	@Test
	void getRageCostPct() {
		accumulateTestAttributes(RAGE_COST_PCT);
		assertThat(costStats.getRageCostPct()).isEqualTo(160);
	}

	@Test
	void getHealthCost() {
		accumulateTestAttributes(HEALTH_COST);
		assertThat(costStats.getHealthCost()).isEqualTo(160);
	}

	@Test
	void getHealthCostPct() {
		accumulateTestAttributes(HEALTH_COST_PCT);
		assertThat(costStats.getHealthCostPct()).isEqualTo(160);
	}

	@Test
	void getPower() {
		accumulateTestAttributes(POWER);
		assertThat(costStats.getPower()).isEqualTo(160);
	}

	@Test
	void getCooldown() {
		accumulateTestAttributes(COOLDOWN);
		assertThat(costStats.getCooldown()).isEqualTo(160);
	}

	@Test
	void getCooldownPct() {
		accumulateTestAttributes(COOLDOWN_PCT);
		assertThat(costStats.getCooldownPct()).isEqualTo(160);
	}

	@Test
	void copy() {
		costStats.accumulateAttribute(MANA_COST, 1);
		costStats.accumulateAttribute(MANA_COST_PCT, 2);
		costStats.accumulateAttribute(ENERGY_COST, 3);
		costStats.accumulateAttribute(ENERGY_COST_PCT, 4);
		costStats.accumulateAttribute(RAGE_COST, 5);
		costStats.accumulateAttribute(RAGE_COST_PCT, 6);
		costStats.accumulateAttribute(HEALTH_COST, 7);
		costStats.accumulateAttribute(HEALTH_COST_PCT, 8);
		costStats.accumulateAttribute(COST_REDUCTION_CT, 9);
		costStats.accumulateAttribute(POWER, 10);
		costStats.accumulateAttribute(COOLDOWN, 11);
		costStats.accumulateAttribute(COOLDOWN_PCT, 12);

		var copy = costStats.copy();

		assertThat(copy.getManaCost()).isEqualTo(costStats.getManaCost());
		assertThat(copy.getManaCostPct()).isEqualTo(costStats.getManaCostPct());
		assertThat(copy.getEnergyCost()).isEqualTo(costStats.getEnergyCost());
		assertThat(copy.getEnergyCostPct()).isEqualTo(costStats.getEnergyCostPct());
		assertThat(copy.getRageCost()).isEqualTo(costStats.getRageCost());
		assertThat(copy.getRageCostPct()).isEqualTo(costStats.getRageCostPct());
		assertThat(copy.getHealthCost()).isEqualTo(costStats.getHealthCost());
		assertThat(copy.getHealthCostPct()).isEqualTo(costStats.getHealthCostPct());
		assertThat(copy.getCostReductionPct()).isEqualTo(costStats.getCostReductionPct());
		assertThat(copy.getPower()).isEqualTo(costStats.getPower());
		assertThat(copy.getCooldown()).isEqualTo(costStats.getCooldown());
		assertThat(copy.getCooldownPct()).isEqualTo(costStats.getCooldownPct());
	}

	void accumulateTestAttributes(PrimitiveAttributeId attributeId) {
		var list = List.of(
				Attribute.of(attributeId, 10),
				Attribute.of(attributeId, 20, MiscCondition.PHYSICAL),
				Attribute.of(attributeId, 30, MiscCondition.SPELL),
				Attribute.of(attributeId, 40)
		);

		costStats.accumulateAttributes(list, 2);
	}

	int level = 70;
	AccumulatedCostStats costStats;

	@BeforeEach
	void setUp() {
		var conditionArgs = new AttributeConditionArgs(ActionType.SPELL);

		this.costStats = new AccumulatedCostStats(conditionArgs, level);
	}
}