package wow.commons.model.spells;

import wow.commons.model.Percent;
import wow.commons.util.EnumUtil;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2020-10-17
 */
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
		HEALING {
			@Override
			public void visit(Visitor visitor) {
				visitor.visitHealing();
			}
		},

		MANA {
			@Override
			public void visit(Visitor visitor) {
				visitor.visitMana();
			}
		};

		public static To parse(String value) {
			return EnumUtil.parse(value, values());
		}

		public interface Visitor {
			void visitHealing();
			void visitMana();
		}

		public abstract void visit(Visitor visitor);
	}

	private final From from;
	private final To to;
	private final Percent percent;

	public Conversion(From from, To to, Percent percent) {
		this.from = from;
		this.to = to;
		this.percent = percent;
	}

	public From getFrom() {
		return from;
	}

	public To getTo() {
		return to;
	}

	public Percent getPercent() {
		return percent;
	}

	public boolean is(From from, To to) {
		return this.from == from && this.to == to;
	}
}
