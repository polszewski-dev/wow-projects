package wow.commons.model.spells;

import wow.commons.model.Percent;
import wow.commons.util.EnumUtil;

import java.util.List;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
public record Conversion(
		wow.commons.model.spells.Conversion.From from,
		wow.commons.model.spells.Conversion.To to,
		Percent percent
) {
	public Conversion {
		Objects.requireNonNull(from);
		Objects.requireNonNull(to);
		Objects.requireNonNull(percent);
	}

	public enum From {
		DAMAGE {
			@Override
			public int getValue(int actualDamage, List<Cost> paidCosts) {
				return actualDamage;
			}
		},

		HEALTH_PAID {
			@Override
			public int getValue(int actualDamage, List<Cost> paidCosts) {
				for (Cost paidCost : paidCosts) {
					if (paidCost.type() == CostType.HEALTH) {
						return paidCost.amount();
					}
				}
				return 0;
			}
		};

		public abstract int getValue(int actualDamage, List<Cost> paidCosts);

		public static From parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	public enum To {
		HEALING,
		MANA;

		public static To parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	public boolean is(From from, To to) {
		return this.from == from && this.to == to;
	}
}
