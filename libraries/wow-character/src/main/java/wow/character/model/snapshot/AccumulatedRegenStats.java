package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.util.AttributeConditionArgs;
import wow.commons.model.attribute.AttributeId;

/**
 * User: POlszewski
 * Date: 2025-10-12
 */
@Getter
public class AccumulatedRegenStats extends AccumulatedPartialStats {
	private double hp5;
	private double mp5;
	private double healthGeneratedPct;
	private double healthRegenPct;
	private double manaRegenPct;
	private double inCombatHealthRegenPct;
	private double inCombatManaRegenPct;

	public AccumulatedRegenStats(AttributeConditionArgs conditionArgs) {
		super(conditionArgs);
	}

	private AccumulatedRegenStats(AccumulatedRegenStats stats) {
		super(stats);
		this.hp5 = stats.hp5;
		this.mp5 = stats.mp5;
		this.healthGeneratedPct = stats.healthGeneratedPct;
		this.healthRegenPct = stats.healthRegenPct;
		this.manaRegenPct = stats.manaRegenPct;
		this.inCombatHealthRegenPct = stats.inCombatHealthRegenPct;
		this.inCombatManaRegenPct = stats.inCombatManaRegenPct;
	}

	@Override
	public void accumulateAttribute(AttributeId id, double value) {
		switch (id) {
			case HP5:
				this.hp5 += value;
				break;
			case MP5, PARTY_MP5:
				this.mp5 += value;
				break;
			case HEALTH_GENERATED_PCT:
				this.healthGeneratedPct += value;
				break;
			case HEALTH_REGEN_PCT:
				this.healthRegenPct += value;
				break;
			case MANA_REGEN_PCT:
				this.manaRegenPct += value;
				break;
			case IN_COMBAT_HEALTH_REGEN_PCT:
				this.inCombatHealthRegenPct += value;
				break;
			case IN_COMBAT_MANA_REGEN_PCT:
				this.inCombatManaRegenPct += value;
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedRegenStats copy() {
		return new AccumulatedRegenStats(this);
	}
}
