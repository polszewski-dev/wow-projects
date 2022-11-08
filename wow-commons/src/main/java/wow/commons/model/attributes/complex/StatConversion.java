package wow.commons.model.attributes.complex;

import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.ComplexAttribute;
import wow.commons.model.attributes.ConditionalAttribute;
import wow.commons.util.EnumUtil;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public class StatConversion extends ComplexAttribute implements ConditionalAttribute {
	public enum Stat {
		PET_STA,
		PET_INT,
		SP;

		public static Stat parse(String value) {
			return EnumUtil.parse(value, values());
		}
	}

	private final Stat fromStat;
	private final Stat toStat;
	private final Percent ratioPct;
	private final AttributeCondition condition;

	public StatConversion(Stat fromStat, Stat toStat, Percent ratioPct, AttributeCondition condition) {
		super(AttributeId.STAT_CONVERSION);
		this.fromStat = fromStat;
		this.toStat = toStat;
		this.ratioPct = ratioPct;
		this.condition = condition;
	}

	public Stat getFromStat() {
		return fromStat;
	}

	public Stat getToStat() {
		return toStat;
	}

	public Percent getRatioPct() {
		return ratioPct;
	}

	@Override
	public AttributeCondition getCondition() {
		return condition;
	}

	@Override
	public StatConversion attachCondition(AttributeCondition condition) {
		return new StatConversion(fromStat, toStat, ratioPct, condition);
	}

	@Override
	public String toString() {
		return String.format("(from: %s, to: %s, ratio: %s)%s", fromStat, toStat, ratioPct, getConditionString());
	}
}
