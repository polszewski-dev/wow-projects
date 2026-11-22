package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.effect.component.StatConversionType;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-10-12
 */
@Getter
public class AccumulatedRegenStats extends AccumulatedPartialStats {
	private double hp5;
	private double mp5;
	private double healingTakenPct;
	private double healthRegenPct;
	private double manaRegenPct;
	private double inCombatHealthRegenPct;
	private double inCombatManaRegenPct;

	private final AccumulatedBaseStats accumulatedBaseStats;

	private AccumulatedRegenStats(AttributeConditionChecker checker) {
		super(checker);
		this.accumulatedBaseStats = new AccumulatedBaseStats(checker);
	}

	private AccumulatedRegenStats(AccumulatedRegenStats stats) {
		super(stats);
		this.hp5 = stats.hp5;
		this.mp5 = stats.mp5;
		this.healingTakenPct = stats.healingTakenPct;
		this.healthRegenPct = stats.healthRegenPct;
		this.manaRegenPct = stats.manaRegenPct;
		this.inCombatHealthRegenPct = stats.inCombatHealthRegenPct;
		this.inCombatManaRegenPct = stats.inCombatManaRegenPct;
		this.accumulatedBaseStats = stats.accumulatedBaseStats.copy();
	}

	public static AccumulatedRegenStats forRegen(Character owner) {
		Objects.requireNonNull(owner);

		var checker = new AttributeConditionChecker(owner, null, null, null, null, null);

		return new AccumulatedRegenStats(checker);
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case HP5:
				this.hp5 += getValue(attribute, scaleFactor);
				break;
			case MP5:
				this.mp5 += getValue(attribute, scaleFactor);
				break;
			case HEALING_TAKEN_PCT:
				this.healingTakenPct += getValue(attribute, scaleFactor);
				break;
			case HEALTH_REGEN_PCT:
				this.healthRegenPct += getValue(attribute, scaleFactor);
				break;
			case MANA_REGEN_PCT:
				this.manaRegenPct += getValue(attribute, scaleFactor);
				break;
			case IN_COMBAT_HEALTH_REGEN_PCT:
				this.inCombatHealthRegenPct += getValue(attribute, scaleFactor);
				break;
			case IN_COMBAT_MANA_REGEN_PCT:
				this.inCombatManaRegenPct += getValue(attribute, scaleFactor);
				break;
			default:
				accumulatedBaseStats.accumulateAttribute(attribute, scaleFactor);
		}
	}

	public AccumulatedRegenStats copy() {
		return new AccumulatedRegenStats(this);
	}

	@Override
	protected double getValueFrom(StatConversionType type) {
		return accumulatedBaseStats.getValueFrom(type);
	}
}
