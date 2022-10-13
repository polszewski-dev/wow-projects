package wow.commons.model.attributes.complex;

import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.ComplexAttribute;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public class StatConversion extends ComplexAttribute {
	public enum Stat {
		PET_STA, PET_INT, SP;

		public static Stat parse(String value) {
			if (value == null || value.isEmpty()) {
				return null;
			}
			return valueOf(value.toUpperCase());
		}
	}

	private final Stat fromStat;
	private final Stat toStat;
	private final Percent ratioPct;

	public StatConversion(Stat fromStat, Stat toStat, Percent ratioPct, AttributeCondition condition) {
		super(AttributeId.statConversion, condition);
		this.fromStat = fromStat;
		this.toStat = toStat;
		this.ratioPct = ratioPct;
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
	public StatConversion attachCondition(AttributeCondition condition) {
		return new StatConversion(fromStat, toStat, ratioPct, condition);
	}

	@Override
	public String toString() {
		return String.format("(from: %s, to: %s, ratio: %s)", fromStat, toStat, ratioPct);
	}
}
