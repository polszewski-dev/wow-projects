package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;

import java.util.Objects;

import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.spell.ActionType.SPELL;
import static wow.commons.model.spell.component.ComponentCommand.DirectCommand;
import static wow.commons.model.spell.component.ComponentCommand.PeriodicCommand;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public class AccumulatedTargetStats extends AccumulatedPartialTargetStats {
	private double amountTaken;
	private double amountTakenPct;
	private double powerTaken;
	private double critTakenPct;

	private final boolean isDamage;
	private final boolean isHealing;

	private AccumulatedTargetStats(Character target, Spell spell, ActionType actionType, PowerType powerType, SpellSchool spellSchool) {
		super(target, spell, actionType, powerType, spellSchool);
		this.isDamage = powerType != null && powerType != HEALING;
		this.isHealing = powerType == HEALING;
	}

	private AccumulatedTargetStats(AccumulatedTargetStats stats) {
		super(stats);
		this.amountTaken = stats.amountTaken;
		this.amountTakenPct = stats.amountTakenPct;
		this.powerTaken = stats.powerTaken;
		this.critTakenPct = stats.critTakenPct;
		this.isDamage = stats.isDamage;
		this.isHealing = stats.isHealing;
	}

	public static AccumulatedTargetStats forSpellTarget(Character target, Spell spell, PowerType powerType) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);

		return new AccumulatedTargetStats(target, spell, SPELL, powerType, null);
	}

	public static AccumulatedTargetStats forSpellTarget(Character target, Spell spell, PowerType powerType, DirectCommand command) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);

		return new AccumulatedTargetStats(target, spell, SPELL, powerType, command.school());
	}

	public static AccumulatedTargetStats forSpellTarget(Character target, Spell spell, PowerType powerType, PeriodicCommand command) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);

		return new AccumulatedTargetStats(target, spell, SPELL, powerType, command.school());
	}

	public static AccumulatedTargetStats forAnySpellTarget(Character target) {
		Objects.requireNonNull(target);

		return new AccumulatedTargetStats(target, null, SPELL, null, null);
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
