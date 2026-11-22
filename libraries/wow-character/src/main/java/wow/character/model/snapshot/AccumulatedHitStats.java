package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.component.StatConversionType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;

import java.util.Objects;

import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
@Getter
public class AccumulatedHitStats extends AccumulatedPartialCasterStats {
	private double hitRating;
	private double hitPct;

	private AccumulatedHitStats(Character caster, Spell spell, Character target, ActionType actionType, PowerType powerType, SpellSchool spellSchool) {
		super(caster, spell, target, actionType, powerType, spellSchool);
	}

	private AccumulatedHitStats(AccumulatedHitStats stats) {
		super(stats);
		this.hitRating = stats.hitRating;
		this.hitPct = stats.hitPct;
	}

	public static AccumulatedHitStats forSpell(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		return new AccumulatedHitStats(caster, spell, target, SPELL, null, null);
	}

	public static AccumulatedHitStats forAnySpell(Character caster) {
		Objects.requireNonNull(caster);

		return new AccumulatedHitStats(caster, null, null, SPELL, null, null);
	}

	public static AccumulatedHitStats forMaster(Character master, AccumulatedHitStats petStats) {
		var stats = new AccumulatedHitStats(master, petStats.spell, petStats.target, petStats.actionType, petStats.powerType, petStats.spellSchool);

		stats.copyFrom(petStats);
		return stats;
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case HIT_RATING:
				this.hitRating += getValue(attribute, scaleFactor);
				break;
			case HIT_PCT:
				this.hitPct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	public AccumulatedHitStats copy() {
		return new AccumulatedHitStats(this);
	}

	@Override
	protected double getValueFrom(StatConversionType type) {
		return switch (type) {
			case MASTER_HIT_PCT_TO_HIT_PCT

					-> hitPct;

			case MASTER_HIT_RATING_TO_HIT_RATING

					-> hitRating;

			default

					-> super.getValueFrom(type);
		};
	}
}
