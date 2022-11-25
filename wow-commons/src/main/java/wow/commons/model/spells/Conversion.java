package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.Percent;
import wow.commons.util.EnumUtil;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
@AllArgsConstructor
@Getter
public class Conversion {
	public enum From {
		DAMAGE{
			@Override
			public int getValue(int actualDamage, List<Cost> paidCosts) {
				return actualDamage;
			}
		},

		HEALTH_PAID {
			@Override
			public int getValue(int actualDamage, List<Cost> paidCosts) {
				for (Cost paidCost : paidCosts) {
					if (paidCost.getType() == CostType.HEALTH) {
						return paidCost.getAmount();
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

	@NonNull
	private final From from;
	@NonNull
	private final To to;
	@NonNull
	private final Percent percent;

	public boolean is(From from, To to) {
		return this.from == from && this.to == to;
	}
}
