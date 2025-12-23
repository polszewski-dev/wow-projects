package wow.commons.model.spell.component;

import wow.commons.model.AnyDuration;
import wow.commons.model.Percent;
import wow.commons.model.character.PetType;
import wow.commons.model.effect.Effect;
import wow.commons.model.spell.*;
import wow.commons.util.EnumUtil;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2025-11-30
 */
public sealed interface ComponentCommand {
	SpellTarget target();

	default boolean isSingleTarget() {
		return target().isSingle();
	}

	default boolean isAoE() {
		return target().isAoE();
	}

	interface HasSecondaryTargetCondition {
		SpellTargetCondition condition();
	}

	sealed interface DirectCommand extends ComponentCommand, HasSecondaryTargetCondition {
		default SpellSchool school() {
			return null;
		}
	}

	sealed interface PeriodicCommand extends ComponentCommand {
		default SpellSchool school() {
			return null;
		}
	}

	sealed interface ChangeHealthDirectly extends DirectCommand {
		Coefficient coefficient();

		int min();

		int max();

		default SpellSchool school() {
			return coefficient().school();
		}

		DirectComponentBonus bonus();
	}

	sealed interface ChangeManaDirectly extends DirectCommand {
		Coefficient coefficient();

		int min();

		int max();

		default SpellSchool school() {
			return coefficient().school();
		}
	}

	sealed interface ChangeHealthPeriodically extends PeriodicCommand {
		Coefficient coefficient();

		int amount();

		int numTicks();

		TickScheme tickScheme();

		default SpellSchool school() {
			return coefficient().school();
		}
	}

	sealed interface ChangeManaPeriodically extends PeriodicCommand {
		Coefficient coefficient();

		int amount();

		int numTicks();

		default SpellSchool school() {
			return coefficient().school();
		}
	}

	record DealDamageDirectly(
			SpellTarget target,
			SpellTargetCondition condition,
			Coefficient coefficient,
			int min,
			int max,
			DirectComponentBonus bonus
	) implements ChangeHealthDirectly {
		public DealDamageDirectly {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
			Objects.requireNonNull(coefficient);
		}
	}

	record HealDirectly(
			SpellTarget target,
			SpellTargetCondition condition,
			Coefficient coefficient,
			int min,
			int max,
			DirectComponentBonus bonus
	) implements ChangeHealthDirectly {
		public HealDirectly {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
			Objects.requireNonNull(coefficient);
		}
	}

	record LoseManaDirectly(
			SpellTarget target,
			SpellTargetCondition condition,
			Coefficient coefficient,
			int min,
			int max
	) implements ChangeManaDirectly {
		public LoseManaDirectly {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
			Objects.requireNonNull(coefficient);
		}
	}

	record GainManaDirectly(
			SpellTarget target,
			SpellTargetCondition condition,
			Coefficient coefficient,
			int min,
			int max
	) implements ChangeManaDirectly {
		public GainManaDirectly {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
			Objects.requireNonNull(coefficient);
		}
	}

	record GainEnergyDirectly(
			SpellTarget target,
			SpellTargetCondition condition,
			int amount
	) implements DirectCommand {
		public GainEnergyDirectly {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
		}
	}

	record GainRageDirectly(
			SpellTarget target,
			SpellTargetCondition condition,
			int amount
	) implements DirectCommand {
		public GainRageDirectly {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
		}
	}

	record RefundCostPctDirectly(
			SpellTarget target,
			SpellTargetCondition condition,
			Percent ratio
	) implements DirectCommand {
		public RefundCostPctDirectly {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
			Objects.requireNonNull(ratio);
		}
	}
	record GainBaseManaPct(
			SpellTarget target,
			SpellTargetCondition condition,
			Percent ratio
	) implements DirectCommand {
		public GainBaseManaPct {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
			Objects.requireNonNull(ratio);
		}
	}

	record ReduceThreatDirectly(
			SpellTarget target,
			SpellTargetCondition condition,
			int amount
	) implements DirectCommand {
		public ReduceThreatDirectly {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
		}
	}

	record SummonPet(
			SpellTarget target,
			SpellTargetCondition condition,
			PetType petType
	) implements DirectCommand {
		public SummonPet {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
			Objects.requireNonNull(petType);
		}
	}

	record SacrificePet(
			SpellTarget target,
			SpellTargetCondition condition
	) implements DirectCommand {
		public SacrificePet {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
		}
	}

	enum From {
		DAMAGE,
		HEAL,
		MANA_LOSS,
		MANA_GAIN,
		HEALTH_PAID,
		PARENT_DAMAGE,
		PARENT_MANA_LOSS,
		PARENT_MANA_GAIN,
		PARENT_DAMAGE_ABSORBED,
		;

		public static From parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	enum To {
		DAMAGE,
		HEAL,
		MANA_LOSS,
		MANA_GAIN;

		public static To parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	record Copy(
			SpellTarget target,
			SpellTargetCondition condition,
			SpellSchool school,
			From from,
			To to,
			Percent ratio
	) implements DirectCommand, PeriodicCommand {
		public Copy {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
			Objects.requireNonNull(from);
			Objects.requireNonNull(to);
			Objects.requireNonNull(ratio);
		}
	}

	record ExtraAttacks(
			SpellTarget target,
			SpellTargetCondition condition,
			int amount
	) implements DirectCommand {
		public ExtraAttacks {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
		}
	}

	record DealDamagePeriodically(
			SpellTarget target,
			Coefficient coefficient,
			int amount,
			int numTicks,
			TickScheme tickScheme
	) implements ChangeHealthPeriodically {
		public DealDamagePeriodically {
			Objects.requireNonNull(target);
			Objects.requireNonNull(coefficient);
			Objects.requireNonNull(tickScheme);
		}
	}

	record HealPeriodically(
			SpellTarget target,
			Coefficient coefficient,
			int amount,
			int numTicks,
			TickScheme tickScheme
	) implements ChangeHealthPeriodically {
		public HealPeriodically {
			Objects.requireNonNull(target);
			Objects.requireNonNull(coefficient);
			Objects.requireNonNull(tickScheme);
		}
	}

	record LoseManaPeriodically(
			SpellTarget target,
			Coefficient coefficient,
			int amount,
			int numTicks
	) implements ChangeManaPeriodically {
		public LoseManaPeriodically {
			Objects.requireNonNull(target);
			Objects.requireNonNull(coefficient);
		}
	}

	record GainManaPeriodically(
			SpellTarget target,
			Coefficient coefficient,
			int amount,
			int numTicks
	) implements ChangeManaPeriodically {
		public GainManaPeriodically {
			Objects.requireNonNull(target);
			Objects.requireNonNull(coefficient);
		}
	}

	record GainPctOfTotalManaPeriodically(
			SpellTarget target,
			Coefficient coefficient,
			int amount,
			int numTicks
	) implements ChangeManaPeriodically {
		public GainPctOfTotalManaPeriodically {
			Objects.requireNonNull(target);
			Objects.requireNonNull(coefficient);
		}
	}

	record HealPctOfDamageTakenPeriodically(
			SpellTarget target,
			Coefficient coefficient,
			int amount,
			int numTicks,
			TickScheme tickScheme
	) implements ChangeHealthPeriodically {
		public HealPctOfDamageTakenPeriodically {
			Objects.requireNonNull(target);
			Objects.requireNonNull(coefficient);
		}
	}

	record AddStackPeriodically(
			SpellTarget target
	) implements PeriodicCommand {
		public AddStackPeriodically {
			Objects.requireNonNull(target);
		}
	}

	record DealCounterDamagePeriodically(
			SpellTarget target,
			Coefficient coefficient,
			int numTicks
	) implements PeriodicCommand {
		public DealCounterDamagePeriodically {
			Objects.requireNonNull(target);
		}
	}

	enum CounterScaling {
		DEFAULT,
		LAST_DAMAGE_DONE_PCT;

		public static CounterScaling parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	record CounterParams(int number, CounterScaling scaling) {
		public static CounterParams EMPTY = new CounterParams(0, CounterScaling.DEFAULT);

		public CounterParams {
			Objects.requireNonNull(scaling);
		}
	}

	record ApplyEffect(
			SpellTarget target,
			SpellTargetCondition condition,
			Effect effect,
			AnyDuration duration,
			int numStacks,
			int numCharges,
			CounterParams counterParams,
			EffectReplacementMode replacementMode
	) implements ComponentCommand, HasSecondaryTargetCondition {
		public ApplyEffect {
			Objects.requireNonNull(target);
			Objects.requireNonNull(condition);
			Objects.requireNonNull(effect);
			Objects.requireNonNull(duration);
			Objects.requireNonNull(replacementMode);
		}
	}
}
