package wow.commons.model.attributes.complex;

import lombok.Getter;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
@Getter
public class StatConversion extends ComplexAttribute {
	private final PrimitiveAttributeId fromStat;
	private final PrimitiveAttributeId toStat;
	private final Percent ratioPct;

	public StatConversion(PrimitiveAttributeId fromStat, PrimitiveAttributeId toStat, Percent ratioPct, AttributeCondition condition) {
		super(ComplexAttributeId.STAT_CONVERSION, condition);
		this.fromStat = fromStat;
		this.toStat = toStat;
		this.ratioPct = ratioPct;
	}

	@Override
	public StatConversion attachCondition(AttributeCondition condition) {
		return new StatConversion(fromStat, toStat, ratioPct, condition);
	}

	@Override
	protected String doToString() {
		return String.format("(from: %s, to: %s, ratio: %s)", fromStat, toStat, ratioPct);
	}
}
