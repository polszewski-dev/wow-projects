package wow.commons.model.spell.component;

import wow.commons.model.AnyDuration;
import wow.commons.model.Percent;
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
		SpellSchool school();
	}

	sealed interface PeriodicCommand extends ComponentCommand {
		SpellSchool school();
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

		public SpellSchool school() {
			return null;
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

		public SpellSchool school() {
			return null;
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

		@Override
		public SpellSchool school() {
			return null;
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

		@Override
		public SpellSchool school() {
			return null;
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

		public SpellSchool school() {
			return null;
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

		public SpellSchool school() {
			return null;
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

		@Override
		public SpellSchool school() {
			return null;
		}
	}

	record ApplyEffect(
			SpellTarget target,
			SpellTargetCondition condition,
			Effect effect,
			AnyDuration duration,
			int numStacks,
			int numCharges,
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
