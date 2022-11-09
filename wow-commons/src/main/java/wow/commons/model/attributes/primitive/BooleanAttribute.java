package wow.commons.model.attributes.primitive;

import wow.commons.model.attributes.AttributeCondition;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public final class BooleanAttribute extends PrimitiveAttribute {
	private final BooleanAttributeId id;
	private final boolean value;
	private final AttributeCondition condition;

	public BooleanAttribute(BooleanAttributeId id, boolean value, AttributeCondition condition) {
		this.id = id;
		this.value = value;
		this.condition = condition;
	}

	@Override
	public BooleanAttributeId getId() {
		return id;
	}

	@Override
	public boolean getBoolean() {
		return value;
	}

	@Override
	public AttributeCondition getCondition() {
		return condition;
	}

	@Override
	public BooleanAttribute attachCondition(AttributeCondition condition) {
		return new BooleanAttribute(id, value, condition);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof BooleanAttribute)) return false;
		BooleanAttribute that = (BooleanAttribute) o;
		return value == that.value && id == that.id && Objects.equals(condition, that.condition);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, value, condition);
	}

	@Override
	public String toString() {
		return String.format("%s = %s%s", id, value, getConditionString());
	}
}
