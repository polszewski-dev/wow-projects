package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.Spell;

import java.util.Objects;

import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2023-11-04
 */
@Getter
public class AccumulatedReceivedEffectStats extends AccumulatedPartialStats {
	private double receivedEffectDuration;
	private double receivedEffectDurationPct;

	private AccumulatedReceivedEffectStats(AttributeConditionChecker checker) {
		super(checker);
	}

	private AccumulatedReceivedEffectStats(AccumulatedReceivedEffectStats stats) {
		super(stats);
		this.receivedEffectDuration = stats.receivedEffectDuration;
		this.receivedEffectDurationPct = stats.receivedEffectDurationPct;
	}

	public static AccumulatedReceivedEffectStats forSpellTarget(Character target, Spell spell, Effect effect) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);

		var checker = new AttributeConditionChecker(target, spell, null, SPELL, null, spell.getSchool());

		checker.setAppliedEffect(effect);

		return new AccumulatedReceivedEffectStats(checker);
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
