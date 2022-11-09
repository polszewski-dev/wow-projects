package wow.commons.model.attributes.primitive;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public final class DoubleAttribute extends ScalarAttribute {
	private final double value;

	public DoubleAttribute(AttributeId id, double value, AttributeCondition condition) {
		super(id, condition);
		this.value = value;
		if (!id.isDoubleAttribute()) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public double getDouble() {
		return value;
	}

	@Override
	public DoubleAttribute attachCondition(AttributeCondition condition) {
		return new DoubleAttribute(id, value, condition);
	}

	@Override
	public DoubleAttribute scale(double factor) {
		double scaledValue = factor * value;
		return new DoubleAttribute(id, scaledValue, getCondition());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DoubleAttribute)) return false;
		DoubleAttribute that = (DoubleAttribute) o;
		return id == that.id && Double.compare(that.value, value) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, value);
	}

	@Override
	public String toString() {
		if (value % 1 == 0) {
			return String.format("%s %s%s", (int)value, id, getConditionString());
		} else {
			return String.format("%.2f %s%s", value, id, getConditionString());
		}
	}
}
