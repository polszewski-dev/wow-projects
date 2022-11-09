package wow.commons.model.attributes.primitive;

import wow.commons.model.attributes.AttributeCondition;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public final class DoubleAttribute extends ScalarAttribute {
	private final DoubleAttributeId id;
	private final double value;
	private final AttributeCondition condition;

	public DoubleAttribute(DoubleAttributeId id, double value, AttributeCondition condition) {
		this.id = id;
		this.value = value;
		this.condition = condition;
	}

	@Override
	public DoubleAttributeId getId() {
		return id;
	}

	@Override
	public double getDouble() {
		return value;
	}

	@Override
	public AttributeCondition getCondition() {
		return condition;
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
		return Double.compare(that.value, value) == 0 && id == that.id && Objects.equals(condition, that.condition);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, value, condition);
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
