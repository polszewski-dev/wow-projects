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

	private AccumulatedCastStats(Character caster, Spell spell, Character target, ActionType actionType, PowerType powerType, SpellSchool spellSchool) {
		super(caster, spell, target, actionType, powerType, spellSchool);
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

		return new AccumulatedCastStats(caster, spell, target, SPELL, null, null);
	}

	public static AccumulatedCastStats forAnySpell(Character caster) {
		Objects.requireNonNull(caster);

		return new AccumulatedCastStats(caster, null, null, SPELL, null, null);
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
