package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.component.StatConversionType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;

import java.util.Objects;

import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2022-12-16
 */
@Getter
public class AccumulatedSpellStats extends AccumulatedBaseStats {
	private double amount;
	private double amountPct;
	private double effectPct;
	private double power;
	private double powerPct;
	private double powerCoeffPct;
	private double critRating;
	private double critPct;
	private double critEffectPct;
	private double critEffectMultiplierPct;
	private double critCoeffPct;

	private AccumulatedSpellStats(AttributeConditionChecker checker) {
		super(checker);
		accumulateBaseCrit();
	}

	private AccumulatedSpellStats(AccumulatedSpellStats stats) {
		super(stats);
		this.amount = stats.amount;
		this.amountPct = stats.amountPct;
		this.effectPct = stats.effectPct;
		this.power = stats.power;
		this.powerPct = stats.powerPct;
		this.powerCoeffPct = stats.powerCoeffPct;
		this.critRating = stats.critRating;
		this.critPct = stats.critPct;
		this.critEffectPct = stats.critEffectPct;
		this.critEffectMultiplierPct = stats.critEffectMultiplierPct;
		this.critCoeffPct = stats.critCoeffPct;
	}

	public static AccumulatedSpellStats forAnySpell(Character caster) {
		Objects.requireNonNull(caster);

		var checker = new AttributeConditionChecker(caster, null, null, SPELL, null, null);

		return new AccumulatedSpellStats(checker);
	}

	public static AccumulatedSpellStats forAnySpell(Character caster, PowerType powerType, SpellSchool spellSchool) {
		Objects.requireNonNull(caster);

		var checker = new AttributeConditionChecker(caster, null, null, SPELL, powerType, spellSchool);

		return new AccumulatedSpellStats(checker);
	}

	public static AccumulatedSpellStats forDirectSpell(Character caster, Spell spell, Character target, PowerType powerType, SpellSchool spellSchool) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var checker = new AttributeConditionChecker(caster, spell, target, SPELL, powerType, spellSchool);

		checker.setDirect(true);

		return new AccumulatedSpellStats(checker);
	}

	public static AccumulatedSpellStats forPeriodicSpell(Character caster, Spell spell, Character target, PowerType powerType, SpellSchool spellSchool) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var checker = new AttributeConditionChecker(caster, spell, target, SPELL, powerType, spellSchool);

		checker.setPeriodic(true);

		return new AccumulatedSpellStats(checker);
	}

	public static AccumulatedSpellStats forMaster(Character master, AccumulatedPartialStats stats) {
		var checker = stats.getChecker().withCaster(master);

		return new AccumulatedSpellStats(checker);
	}

	private void accumulateBaseCrit() {
		var baseStatInfo = checker.getCaster().getBaseStatInfo();

		critPct += baseStatInfo.getBaseSpellCritPct().value();
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case DAMAGE:
				if (isDamage) {
					this.amount += getValue(attribute, scaleFactor);
				}
				break;
			case DAMAGE_PCT:
				if (isDamage) {
					this.amountPct += getValue(attribute, scaleFactor);
				}
				break;
			case HEALING:
				if (isHealing) {
					this.amount += getValue(attribute, scaleFactor);
				}
				break;
			case HEALING_PCT:
				if (isHealing) {
					this.amountPct += getValue(attribute, scaleFactor);
				}
				break;
			case EFFECT_PCT:
				this.effectPct += getValue(attribute, scaleFactor);
				break;
			case POWER:
				this.power += getValue(attribute, scaleFactor);
				break;
			case POWER_PCT:
				this.powerPct += getValue(attribute, scaleFactor);
				break;
			case POWER_COEFFICIENT_PCT:
				this.powerCoeffPct += getValue(attribute, scaleFactor);
				break;
			case CRIT_RATING:
				this.critRating += getValue(attribute, scaleFactor);
				break;
			case CRIT_PCT:
				this.critPct += getValue(attribute, scaleFactor);
				break;
			case CRIT_EFFECT_PCT:
				this.critEffectPct += getValue(attribute, scaleFactor);
				break;
			case CRIT_EFFECT_MULTIPLIER_PCT:
				this.critEffectMultiplierPct += getValue(attribute, scaleFactor);
				break;
			case CRIT_COEFF_PCT:
				this.critCoeffPct += getValue(attribute, scaleFactor);
				break;
			default:
				super.accumulateAttribute(attribute, scaleFactor);
		}
	}

	@Override
	public AccumulatedSpellStats copy() {
		return new AccumulatedSpellStats(this);
	}

	@Override
	protected double getValueFrom(StatConversionType type) {
		return switch (type) {
			case
					MASTER_POWER_TO_SPELL_DAMAGE,
					MASTER_POWER_TO_ATTACK_POWER

					-> power;

			default

					-> super.getValueFrom(type);
		};
	}
}
