package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.spell.Spell;

import java.util.Objects;

import static wow.commons.model.spell.ActionType.SPELL;
import static wow.commons.model.spell.component.ComponentCommand.ApplyEffect;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
@Getter
public class AccumulatedDurationStats extends AccumulatedPartialCasterStats {
	private double duration;
	private double durationPct;
	private double hasteRating;
	private double hastePct;

	private AccumulatedDurationStats(Character caster, Spell spell, Character target, AttributeConditionChecker checker) {
		super(caster, spell, target, checker);
	}

	private AccumulatedDurationStats(AccumulatedDurationStats stats) {
		super(stats);
		this.duration = stats.duration;
		this.durationPct = stats.durationPct;
		this.hasteRating = stats.hasteRating;
		this.hastePct = stats.hastePct;
	}

	public static AccumulatedDurationStats forSpellEffect(Character caster, Spell spell, Character target, ApplyEffect command) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var checker = new AttributeConditionChecker(caster, spell, target, SPELL, null, spell.getSchool());

		checker.setAppliedEffect(command.effect());

		return new AccumulatedDurationStats(caster, spell, target, checker);
	}

	public static AccumulatedDurationStats forSpellSummon(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var checker = new AttributeConditionChecker(caster, spell, target, SPELL, null, spell.getSchool());

		return new AccumulatedDurationStats(caster, spell, target, checker);
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case DURATION:
				this.duration += getValue(attribute, scaleFactor);
				break;
			case DURATION_PCT:
				this.durationPct += getValue(attribute, scaleFactor);
				break;
			case HASTE_RATING:
				this.hasteRating += getValue(attribute, scaleFactor);
				break;
			case HASTE_PCT:
				this.hastePct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedDurationStats copy() {
		return new AccumulatedDurationStats(this);
	}
}
