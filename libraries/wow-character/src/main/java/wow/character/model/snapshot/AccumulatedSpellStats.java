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

import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.spell.ActionType.SPELL;
import static wow.commons.model.spell.component.ComponentCommand.ChangeHealthDirectly;
import static wow.commons.model.spell.component.ComponentCommand.PeriodicCommand;

/**
 * User: POlszewski
 * Date: 2022-12-16
 */
@Getter
public class AccumulatedSpellStats extends AccumulatedPartialCasterStats {
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

	private final boolean isDamage;
	private final boolean isHealing;

	private final AccumulatedBaseStats accumulatedBaseStats;

	private AccumulatedSpellStats(Character caster, Spell spell, Character target, ActionType actionType, PowerType powerType, SpellSchool spellSchool) {
		super(caster, spell, target, actionType, powerType, spellSchool);
		this.isDamage = powerType != null && powerType != HEALING;
		this.isHealing = powerType == HEALING;
		this.accumulatedBaseStats = new AccumulatedBaseStats(caster);
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
		this.accumulatedBaseStats = stats.accumulatedBaseStats.copy();
		this.isDamage = stats.isDamage;
		this.isHealing = stats.isHealing;
	}

	public static AccumulatedSpellStats forAnySpell(Character caster) {
		Objects.requireNonNull(caster);

		return new AccumulatedSpellStats(caster, null, null, SPELL, null, null);
	}

	public static AccumulatedSpellStats forAnySpell(Character caster, PowerType powerType, SpellSchool spellSchool) {
		Objects.requireNonNull(caster);

		return new AccumulatedSpellStats(caster, null, null, SPELL, powerType, spellSchool);
	}

	public static AccumulatedSpellStats forDirectSpell(Character caster, Spell spell, Character target, PowerType powerType, ChangeHealthDirectly command) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var stats = new AccumulatedSpellStats(caster, spell, target, SPELL, powerType, command.school());

		stats.direct = true;

		return stats;
	}

	public static AccumulatedSpellStats forPeriodicSpell(Character caster, Spell spell, Character target, PowerType powerType, PeriodicCommand command) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var stats = new AccumulatedSpellStats(caster, spell, target, SPELL, powerType, command.school());

		stats.periodic = true;

		return stats;
	}

	public static AccumulatedSpellStats forMaster(Character master, AccumulatedSpellStats petStats) {
		var stats = new AccumulatedSpellStats(master, petStats.spell, petStats.target, petStats.actionType, petStats.powerType, petStats.spellSchool);

		stats.copyFrom(petStats);
		return stats;
	}

	private void accumulateBaseCrit() {
		var baseStatInfo = caster.getBaseStatInfo();

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
				accumulatedBaseStats.accumulateAttribute(attribute, scaleFactor);
		}
	}

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

					-> accumulatedBaseStats.getValueFrom(type);
		};
	}

	public double getTotalIntellect() {
		return accumulatedBaseStats.getTotalIntellect();
	}
}
