package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.effect.component.StatConversionType;
import wow.commons.model.spell.Spell;

import java.util.Objects;

import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2023-10-25
 */
@Getter
public class AccumulatedHitStats extends AccumulatedPartialStats {
	private double hitRating;
	private double hitPct;

	private AccumulatedHitStats(AttributeConditionChecker checker) {
		super(checker);
	}

	private AccumulatedHitStats(AccumulatedHitStats stats) {
		super(stats);
		this.hitRating = stats.hitRating;
		this.hitPct = stats.hitPct;
	}

	public static AccumulatedHitStats forSpell(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var checker = new AttributeConditionChecker(caster, spell, target, SPELL, null, spell.getSchool());

		return new AccumulatedHitStats(checker);
	}

	public static AccumulatedHitStats forAnySpell(Character caster) {
		Objects.requireNonNull(caster);

		var checker = new AttributeConditionChecker(caster, null, null, SPELL, null, null);

		return new AccumulatedHitStats(checker);
	}

	public static AccumulatedHitStats forMaster(Character master, AccumulatedPartialStats stats) {
		var checker = stats.getChecker().withCaster(master);

		return new AccumulatedHitStats(checker);
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
