package wow.commons.model.attributes;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.primitive.PrimitiveAttribute;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;

/**
 * User: POlszewski
 * Date: 2021-10-07
 */
public abstract class Attribute {
	public static PrimitiveAttribute of(PrimitiveAttributeId id, double value) {
		return of(id, value, null);
	}

	public static PrimitiveAttribute of(PrimitiveAttributeId id, double value, AttributeCondition condition) {
		return new PrimitiveAttribute(id, value, condition);
	}

	public static PrimitiveAttribute of(PrimitiveAttributeId id, Percent value) {
		return of(id, value, null);
	}

	public static PrimitiveAttribute of(PrimitiveAttributeId id, Percent value, AttributeCondition condition) {
		return new PrimitiveAttribute(id, value, condition);
	}

	public static PrimitiveAttribute of(PrimitiveAttributeId id, Duration value) {
		return of(id, value, null);
	}

	public static PrimitiveAttribute of(PrimitiveAttributeId id, Duration value, AttributeCondition condition) {
		return new PrimitiveAttribute(id, value, condition);
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, double value) {
		return ofNullable(id, value, null);
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, double value, AttributeCondition condition) {
		return value != 0 ? of(id, value, condition) : null;
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Percent value) {
		return ofNullable(id, value, null);
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Percent value, AttributeCondition condition) {
		return value != null && !value.isZero() ? of(id, value, condition) : null;
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Duration value) {
		return ofNullable(id, value, null);
	}

	public static PrimitiveAttribute ofNullable(PrimitiveAttributeId id, Duration value, AttributeCondition condition) {
		return value != null && !value.isZero() ? of(id, value, condition) : null;
	}

	public abstract AttributeId getId();

	public abstract AttributeCondition getCondition();

	public boolean isMatchedBy(AttributeFilter filter) {
		return filter == null || filter.matchesCondition(getCondition());
	}

	@Override
	public abstract String toString();

	protected String getConditionString() {
		return getCondition() != null && !getCondition().isEmpty() ? " | " + getCondition() : "";
	}
}
