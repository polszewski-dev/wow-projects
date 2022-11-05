package wow.commons.model.attributes.primitive;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public final class BooleanAttribute extends PrimitiveAttribute {
	private final boolean value;

	public BooleanAttribute(AttributeId id, boolean value, AttributeCondition condition) {
		super(id, condition);
		this.value = value;
		if (!id.isBooleanAttribute()) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public boolean getBoolean() {
		return value;
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
		return getId() == that.getId() && value == that.value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), value);
	}

	@Override
	public String toString() {
		return String.format("%s = %s%s", id, value, getConditionString());
	}
}
