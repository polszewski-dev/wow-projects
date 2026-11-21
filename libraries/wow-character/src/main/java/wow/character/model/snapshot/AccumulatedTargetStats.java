package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;

import java.util.Objects;

import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public class AccumulatedTargetStats extends AccumulatedPartialStats {
	private double amountTaken;
	private double amountTakenPct;
	private double powerTaken;
	private double critTakenPct;

	private AccumulatedTargetStats(AttributeConditionChecker checker) {
		super(checker);
	}

	private AccumulatedTargetStats(AccumulatedTargetStats stats) {
		super(stats);
		this.amountTaken = stats.amountTaken;
		this.amountTakenPct = stats.amountTakenPct;
		this.powerTaken = stats.powerTaken;
		this.critTakenPct = stats.critTakenPct;
	}

	public static AccumulatedTargetStats forSpellTarget(Character target, Spell spell, PowerType powerType, SpellSchool spellSchool) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);

		var checker = new AttributeConditionChecker(target, spell, null, SPELL, powerType, spellSchool);

		return new AccumulatedTargetStats(checker);
	}

	public static AccumulatedTargetStats emptyStats() {
		return new AccumulatedTargetStats(new AttributeConditionChecker(null, null, null, null, null, null));
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case DAMAGE_TAKEN:
				if (isDamage) {
					this.amountTaken += getValue(attribute, scaleFactor);
				}
				break;
			case DAMAGE_TAKEN_PCT:
				if (isDamage) {
					this.amountTakenPct += getValue(attribute, scaleFactor);
				}
				break;
			case HEALING_TAKEN:
				if (isHealing) {
					this.amountTaken += getValue(attribute, scaleFactor);
				}
				break;
			case HEALING_TAKEN_PCT:
				if (isHealing) {
					this.amountTakenPct += getValue(attribute, scaleFactor);
				}
				break;
			case POWER_TAKEN:
				this.powerTaken += getValue(attribute, scaleFactor);
				break;
			case CRIT_TAKEN_PCT:
				this.critTakenPct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedTargetStats copy() {
		return new AccumulatedTargetStats(this);
	}
}
