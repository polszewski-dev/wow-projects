package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeCondition;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.*;
import wow.commons.model.spell.component.ComponentCommand;
import wow.commons.model.talent.TalentTree;

import java.util.Objects;

import static wow.character.model.snapshot.AccumulatedOwnerStats.getPrimaryStat;
import static wow.commons.model.attribute.AttributeCondition.*;
import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2026-11-22
 */
@Getter
public final class AccumulatedCasterStats extends AccumulatedPartialStats {
	private double baseStamina;
	private double baseIntellect;
	private double baseSpirit;
	private double stamina;
	private double staminaPct;
	private double intellect;
	private double intellectPct;
	private double spirit;
	private double spiritPct;
	private double baseStatsPct;
	private double hasteRating;
	private double hastePct;
	private double castTime;
	private double castTimePct;
	private double manaCost;
	private double manaCostPct;
	private double energyCost;
	private double energyCostPct;
	private double rageCost;
	private double rageCostPct;
	private double healthCost;
	private double healthCostPct;
	private double costReductionPct;
	private double cooldown;
	private double cooldownPct;
	private double hitRating;
	private double hitPct;
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
	private double duration;
	private double durationPct;
	private double copyPct;

	private final Character caster;
	private final Spell spell;
	private final Character target;

	private final ActionType actionType;
	private final PowerType powerType;
	private final SpellSchool spellSchool;

	private final boolean isDamage;
	private final boolean isHealing;

	private boolean direct;
	private boolean periodic;

	private Effect appliedEffect;

	private AccumulatedCasterStats(Character caster, Spell spell, Character target, ActionType actionType, PowerType powerType, SpellSchool spellSchool) {
		this.caster = caster;
		this.spell = spell;
		this.target = target;
		this.actionType = actionType;
		this.powerType = powerType;
		this.spellSchool = spellSchool != null ? spellSchool
				: spell != null ? spell.getSchool()
				: null;
		this.isDamage = powerType != null && powerType != HEALING;
		this.isHealing = powerType == HEALING;
		accumulateBaseStatInfo();
		accumulateBaseCrit();
	}

	public static AccumulatedCasterStats forSpellCast(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		return new AccumulatedCasterStats(caster, spell, target, SPELL, null, null);
	}

	public static AccumulatedCasterStats forAnySpellCast(Character caster) {
		Objects.requireNonNull(caster);

		return new AccumulatedCasterStats(caster, null, null, SPELL, null, null);
	}

	public static AccumulatedCasterStats forSpellCost(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		return new AccumulatedCasterStats(caster, spell, target, SPELL, null, null);
	}

	public static AccumulatedCasterStats forSpellHit(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		return new AccumulatedCasterStats(caster, spell, target, SPELL, null, null);
	}

	public static AccumulatedCasterStats forAnySpellHit(Character caster) {
		Objects.requireNonNull(caster);

		return new AccumulatedCasterStats(caster, null, null, SPELL, null, null);
	}

	public static AccumulatedCasterStats forAnySpellResolution(Character caster) {
		Objects.requireNonNull(caster);

		return new AccumulatedCasterStats(caster, null, null, SPELL, null, null);
	}

	public static AccumulatedCasterStats forAnySpellResolution(Character caster, PowerType powerType, SpellSchool spellSchool) {
		Objects.requireNonNull(caster);

		return new AccumulatedCasterStats(caster, null, null, SPELL, powerType, spellSchool);
	}

	public static AccumulatedCasterStats forDirectSpellResolution(Character caster, Spell spell, Character target, PowerType powerType, ComponentCommand.ChangeHealthDirectly command) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var stats = new AccumulatedCasterStats(caster, spell, target, SPELL, powerType, command.school());

		stats.direct = true;

		return stats;
	}

	public static AccumulatedCasterStats forPeriodicSpellResolution(Character caster, Spell spell, Character target, PowerType powerType, ComponentCommand.PeriodicCommand command) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var stats = new AccumulatedCasterStats(caster, spell, target, SPELL, powerType, command.school());

		stats.periodic = true;

		return stats;
	}

	public static AccumulatedCasterStats withTheSameParams(Character newCaster, AccumulatedCasterStats oldStats) {
		var stats = new AccumulatedCasterStats(newCaster, oldStats.spell, oldStats.target, oldStats.actionType, oldStats.powerType, oldStats.spellSchool);

		stats.direct = oldStats.direct;
		stats.periodic = oldStats.periodic;
		stats.appliedEffect = oldStats.appliedEffect;

		return stats;
	}

	public static AccumulatedCasterStats forSpellEffectDuration(Character caster, Spell spell, Character target, ComponentCommand.ApplyEffect command) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var stats = new AccumulatedCasterStats(caster, spell, target, SPELL, null, null);

		stats.appliedEffect = command.effect();

		return stats;
	}

	public static AccumulatedCasterStats forSpellSummonDuration(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		return new AccumulatedCasterStats(caster, spell, target, SPELL, null, null);
	}

	public static AccumulatedCasterStats forSpellSingleStat(AttributeId attributeId, Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		return new AccumulatedCasterStats(caster, spell, target, SPELL, null, null);
	}

	private AccumulatedCasterStats(AccumulatedCasterStats stats) {
		this(stats.caster, stats.spell, stats.target, stats.actionType, stats.powerType, stats.spellSchool);
		this.direct = stats.direct;
		this.periodic = stats.periodic;
		this.appliedEffect = stats.appliedEffect;
		this.baseStamina = stats.baseStamina;
		this.baseIntellect = stats.baseIntellect;
		this.baseSpirit = stats.baseSpirit;
		this.stamina = stats.stamina;
		this.staminaPct = stats.staminaPct;
		this.intellect = stats.intellect;
		this.intellectPct = stats.intellectPct;
		this.spirit = stats.spirit;
		this.spiritPct = stats.spiritPct;
		this.baseStatsPct = stats.baseStatsPct;
		this.hasteRating = stats.hasteRating;
		this.hastePct = stats.hastePct;
		this.castTime = stats.castTime;
		this.castTimePct = stats.castTimePct;
		this.manaCost = stats.manaCost;
		this.manaCostPct = stats.manaCostPct;
		this.energyCost = stats.energyCost;
		this.energyCostPct = stats.energyCostPct;
		this.rageCost = stats.rageCost;
		this.rageCostPct = stats.rageCostPct;
		this.healthCost = stats.healthCost;
		this.healthCostPct = stats.healthCostPct;
		this.costReductionPct = stats.costReductionPct;
		this.cooldown = stats.cooldown;
		this.cooldownPct = stats.cooldownPct;
		this.hitRating = stats.hitRating;
		this.hitPct = stats.hitPct;
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
		this.duration = stats.duration;
		this.durationPct = stats.durationPct;
		this.copyPct = stats.copyPct;
	}

	public AccumulatedCasterStats copy() {
		return new AccumulatedCasterStats(this);
	}

	private void accumulateBaseStatInfo() {
		var baseStatInfo = caster.getBaseStatInfo();

		baseStamina += baseStatInfo.getBaseStamina();
		baseIntellect += baseStatInfo.getBaseIntellect();
		baseSpirit += baseStatInfo.getBaseSpirit();
	}

	private void accumulateBaseCrit() {
		var baseStatInfo = caster.getBaseStatInfo();

		critPct += baseStatInfo.getBaseSpellCritPct().value();
	}

	@Override
	public int getLevel() {
		return caster.getLevel();
	}

	@Override
	public int getNumberOfEffectsOnTarget(TalentTree tree) {
		if (target == null) {
			return 0;
		}
		return target.getNumberOfEffects(tree);
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		switch (attribute.id()) {
			case STAMINA:
				this.stamina += getValue(attribute, scaleFactor);
				break;
			case STAMINA_PCT:
				this.staminaPct += getValue(attribute, scaleFactor);
				break;
			case INTELLECT:
				this.intellect += getValue(attribute, scaleFactor);
				break;
			case INTELLECT_PCT:
				this.intellectPct += getValue(attribute, scaleFactor);
				break;
			case SPIRIT:
				this.spirit += getValue(attribute, scaleFactor);
				break;
			case SPIRIT_PCT:
				this.spiritPct += getValue(attribute, scaleFactor);
				break;
			case BASE_STATS:
				this.baseStamina += getValue(attribute, scaleFactor);
				this.baseIntellect += getValue(attribute, scaleFactor);
				this.baseSpirit += getValue(attribute, scaleFactor);
				break;
			case BASE_STATS_PCT:
				this.baseStatsPct += getValue(attribute, scaleFactor);
				break;
			case STATS:
				this.stamina += getValue(attribute, scaleFactor);
				this.intellect += getValue(attribute, scaleFactor);
				this.spirit += getValue(attribute, scaleFactor);
				break;
			case STATS_PCT:
				this.staminaPct += getValue(attribute, scaleFactor);
				this.intellectPct += getValue(attribute, scaleFactor);
				this.spiritPct += getValue(attribute, scaleFactor);
				break;
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
			case MANA_COST:
				this.manaCost += getValue(attribute, scaleFactor);
				break;
			case MANA_COST_PCT:
				this.manaCostPct += getValue(attribute, scaleFactor);
				break;
			case ENERGY_COST:
				this.energyCost += getValue(attribute, scaleFactor);
				break;
			case ENERGY_COST_PCT:
				this.energyCostPct += getValue(attribute, scaleFactor);
				break;
			case RAGE_COST:
				this.rageCost += getValue(attribute, scaleFactor);
				break;
			case RAGE_COST_PCT:
				this.rageCostPct += getValue(attribute, scaleFactor);
				break;
			case HEALTH_COST:
				this.healthCost += getValue(attribute, scaleFactor);
				break;
			case HEALTH_COST_PCT:
				this.healthCostPct += getValue(attribute, scaleFactor);
				break;
			case COST_REDUCTION_CT:
				this.costReductionPct += getValue(attribute, scaleFactor);
				break;
			case COOLDOWN:
				this.cooldown += getValue(attribute, scaleFactor);
				break;
			case COOLDOWN_PCT:
				this.cooldownPct += getValue(attribute, scaleFactor);
				break;
			case HIT_RATING:
				this.hitRating += getValue(attribute, scaleFactor);
				break;
			case HIT_PCT:
				this.hitPct += getValue(attribute, scaleFactor);
				break;
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
			case DURATION:
				this.duration += getValue(attribute, scaleFactor);
				break;
			case DURATION_PCT:
				this.durationPct += getValue(attribute, scaleFactor);
				break;
			case COPY_PCT:
				this.copyPct += getValue(attribute, scaleFactor);
				break;
			default:
				// ignore the rest
		}
	}

	@Override
	protected boolean checkCondition(AttributeCondition condition) {
		return switch (condition) {
			case CasterCondition casterCondition ->
					checkCasterCondition(casterCondition);

			case Operator operator ->
					checkConditionOperator(operator);

			default ->
					unsupported(condition);
		};
	}

	private boolean checkCasterCondition(CasterCondition condition) {
		return switch (condition) {
			case AbilityCategoryCondition(var abilityCategory) ->
					spell instanceof Ability a &&
					a.getCategory() == abilityCategory;

			case AbilityIdCondition(var abilityId) ->
					spell instanceof Ability a &&
					a.getAbilityId().equals(abilityId);

			case ActionTypeCondition(var actionTypeArg) ->
					actionType == actionTypeArg;

			case EffectCategoryCondition(var effectCategory) ->
					appliedEffect != null &&
					appliedEffect.getCategory() == effectCategory;

			case EffectNameCondition(var effectName) ->
					appliedEffect != null &&
					appliedEffect.getName().equals(effectName);

			case EmptyCondition() ->
					true;

			case HasCastTime() ->
					spell instanceof Ability a &&
					a.getCastTime().isPositive();

			case HasCastTimeUnder10Sec() ->
					spell instanceof Ability a &&
					a.getCastTime().getSeconds() < 10;

			case HasHealingComponent() ->
					spell != null &&
					spell.hasHealingComponent();

			case HasPet() ->
					caster.getActivePetType() != null;

			case IsDirect() ->
					direct;

			case IsPeriodic() ->
					periodic;

			case IsInstantCast() ->
					spell instanceof Ability a &&
					a.getCastTime().isZero();

			case OwnerHasEffect(var effectName) ->
					caster.hasEffect(effectName);

			case PetTypeCondition(var petType) ->
					caster.getMaster().getActivePetType() == petType;

			case PowerTypeCondition(var powerTypeArg) ->
					powerType == powerTypeArg;

			case SpellSchoolCondition(var spellSchoolArg) ->
					spellSchool == spellSchoolArg;

			case TargetHealthPctLessThan(var value) ->
					target != null &&
					target.getHealthPct().value() < value;

			case TalentTreeCondition(var talentTree) ->
					spell instanceof ClassAbility a &&
					a.getTalentTree() == talentTree;

			case TargetTypeCondition(var creatureType) ->
					target != null && target.getCreatureType() == creatureType;

			case WeaponTypeCondition ignored ->
					false;
		};
	}

	public double getTotalStamina() {
		return getPrimaryStat(baseStamina, baseStatsPct, stamina, staminaPct);
	}

	public double getTotalIntellect() {
		return getPrimaryStat(baseIntellect, baseStatsPct, intellect, intellectPct);
	}

	public double getTotalSpirit() {
		return getPrimaryStat(baseSpirit, baseStatsPct, spirit, spiritPct);
	}

	public double getCost(ResourceType resourceType) {
		return switch (resourceType) {
			case MANA -> manaCost;
			case ENERGY -> energyCost;
			case RAGE -> rageCost;
			case HEALTH -> healthCost;
			case PET_MANA -> throw new UnsupportedOperationException();
		};
	}

	public double getCostPct(ResourceType resourceType) {
		return switch (resourceType) {
			case MANA -> manaCostPct;
			case ENERGY -> energyCostPct;
			case RAGE -> rageCostPct;
			case HEALTH -> healthCostPct;
			case PET_MANA -> throw new UnsupportedOperationException();
		};
	}
}
