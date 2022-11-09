package wow.commons.model.attributes.primitive;

import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public final class PercentAttribute extends ScalarAttribute {
	private final PercentAttributeId id;
	private final Percent value;
	private final AttributeCondition condition;

	public PercentAttribute(PercentAttributeId id, Percent value, AttributeCondition condition) {
		this.id = id;
		this.value = value;
		this.condition = condition;
		if (id == null || value == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public PercentAttributeId getId() {
		return id;
	}

	@Override
	public Percent getPercent() {
		return value;
	}

	@Override
	public AttributeCondition getCondition() {
		return condition;
	}

	@Override
	public Attribute attachCondition(AttributeCondition condition) {
		return new PercentAttribute(id, value, condition);
	}

	@Override
	public PercentAttribute scale(double factor) {
		Percent scaledValue = value.scale(factor);
		return new PercentAttribute(id, scaledValue, getCondition());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PercentAttribute)) return false;
		PercentAttribute that = (PercentAttribute) o;
		return id == that.id && value.equals(that.value) && Objects.equals(condition, that.condition);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, value, condition);
	}

	@Override
	public String toString() {
		return String.format("%s %s%s", value, id, getConditionString());
	}
}
