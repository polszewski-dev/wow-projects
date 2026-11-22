package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.spell.Spell;

import java.util.Objects;

import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2023-10-21
 */
@Getter
public class AccumulatedCastStats extends AccumulatedPartialCasterStats {
	private double hasteRating;
	private double hastePct;
	private double castTime;
	private double castTimePct;

	private AccumulatedCastStats(Character caster, Spell spell, Character target, AttributeConditionChecker checker) {
		super(caster, spell, target, checker);
	}

	private AccumulatedCastStats(AccumulatedCastStats stats) {
		super(stats);
		this.hasteRating = stats.hasteRating;
		this.hastePct = stats.hastePct;
		this.castTime = stats.castTime;
		this.castTimePct = stats.castTimePct;
	}

	public static AccumulatedCastStats forSpell(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var checker = new AttributeConditionChecker(caster, spell, target, SPELL, null, spell.getSchool());

		return new AccumulatedCastStats(caster, spell, target, checker);
	}

	public static AccumulatedCastStats forAnySpell(Character caster) {
		Objects.requireNonNull(caster);

		var checker = new AttributeConditionChecker(caster, null, null, SPELL, null, null);

		return new AccumulatedCastStats(caster, null, null, checker);
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case HASTE_RATING:
				this.hasteRating += getValue(attribute, scaleFactor);
				break;
			case HASTE_PCT:
				this.hastePct += getValue(attribute, scaleFactor);
				break;
			case CAST_TIME:
				this.castTime += getValue(attribute, scaleFactor);
				break;
			case CAST_TIME_PCT:
				this.castTimePct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedCastStats copy() {
		return new AccumulatedCastStats(this);
	}
}
