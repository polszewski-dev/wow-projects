package wow.commons.model.attributes.primitive;

import wow.commons.model.Duration;
import wow.commons.model.attributes.AttributeCondition;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public final class DurationAttribute extends ScalarAttribute {
	private final DurationAttributeId id;
	private final Duration value;
	private final AttributeCondition condition;

	public DurationAttribute(DurationAttributeId id, Duration value, AttributeCondition condition) {
		this.id = id;
		this.value = value;
		this.condition = condition;
		if (id == null || value == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public DurationAttributeId getId() {
		return id;
	}

	@Override
	public Duration getDuration() {
		return value;
	}

	@Override
	public AttributeCondition getCondition() {
		return condition;
	}

	@Override
	public DurationAttribute attachCondition(AttributeCondition condition) {
		return new DurationAttribute(id, value, condition);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DurationAttribute)) return false;
		DurationAttribute that = (DurationAttribute) o;
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
