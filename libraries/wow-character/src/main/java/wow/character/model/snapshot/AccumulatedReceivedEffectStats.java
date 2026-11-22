package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;

import java.util.Objects;

import static wow.commons.model.spell.ActionType.SPELL;
import static wow.commons.model.spell.component.ComponentCommand.ApplyEffect;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public class AccumulatedReceivedEffectStats extends AccumulatedPartialTargetStats {
	private double receivedEffectDuration;
	private double receivedEffectDurationPct;

	private AccumulatedReceivedEffectStats(Character target, Spell spell, ActionType actionType, PowerType powerType, SpellSchool spellSchool) {
		super(target, spell, actionType, powerType, spellSchool);
	}

	private AccumulatedReceivedEffectStats(AccumulatedReceivedEffectStats stats) {
		super(stats);
		this.receivedEffectDuration = stats.receivedEffectDuration;
		this.receivedEffectDurationPct = stats.receivedEffectDurationPct;
	}

	public static AccumulatedReceivedEffectStats forSpellTarget(Character target, Spell spell, ApplyEffect command) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);

		var stats = new AccumulatedReceivedEffectStats(target, spell, SPELL, null, null);

		stats.appliedEffect = command.effect();

		return stats;
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case RECEIVED_EFFECT_DURATION:
				this.receivedEffectDuration += getValue(attribute, scaleFactor);
				break;
			case RECEIVED_EFFECT_DURATION_PCT:
				this.receivedEffectDurationPct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedReceivedEffectStats copy() {
		return new AccumulatedReceivedEffectStats(this);
	}
}
