package wow.commons.model.attributes.complex;

import wow.commons.model.Percent;
import wow.commons.model.attributes.condition.AttributeCondition;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;

import java.util.Objects;

import static wow.commons.util.PrimitiveAttributeFormatter.getConditionString;

/**
 * User: POlszewski
 * Date: 2021-01-17
 */
public record StatConversion(
		PrimitiveAttributeId fromStat,
		PrimitiveAttributeId toStat,
		Percent ratioPct,
		AttributeCondition condition
) implements ComplexAttribute {
	public StatConversion {
		Objects.requireNonNull(fromStat);
		Objects.requireNonNull(toStat);
		Objects.requireNonNull(ratioPct);
		Objects.requireNonNull(condition);
	}

	@Override
	public ComplexAttributeId id() {
		return ComplexAttributeId.STAT_CONVERSION;
	}

	@Override
	public StatConversion attachCondition(AttributeCondition condition) {
		return new StatConversion(fromStat, toStat, ratioPct, condition);
	}

	public String toString() {
		return "(from: %s, to: %s, ratio: %s)".formatted(fromStat, toStat, ratioPct) + getConditionString(condition);
	}
}
