package wow.commons.model.attributes;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.primitive.*;

/**
 * User: POlszewski
 * Date: 2021-10-07
 */
public abstract class Attribute {
	public static DoubleAttribute of(DoubleAttributeId id, double value) {
		return of(id, value, null);
	}

	public static DoubleAttribute of(DoubleAttributeId id, double value, AttributeCondition condition) {
		return new DoubleAttribute(id, value, condition);
	}

	public static PercentAttribute of(PercentAttributeId id, Percent value) {
		return of(id, value, null);
	}

	public static PercentAttribute of(PercentAttributeId id, Percent value, AttributeCondition condition) {
		return new PercentAttribute(id, value, condition);
	}

	public static BooleanAttribute of(BooleanAttributeId id, boolean value) {
		return of(id, value, null);
	}

	public static BooleanAttribute of(BooleanAttributeId id, boolean value, AttributeCondition condition) {
		return new BooleanAttribute(id, value, condition);
	}

	public static DurationAttribute of(DurationAttributeId id, Duration value) {
		return of(id, value, null);
	}

	public static DurationAttribute of(DurationAttributeId id, Duration value, AttributeCondition condition) {
		return new DurationAttribute(id, value, condition);
	}

	public static DoubleAttribute ofNullable(DoubleAttributeId id, double value) {
		return ofNullable(id, value, null);
	}

	public static DoubleAttribute ofNullable(DoubleAttributeId id, double value, AttributeCondition condition) {
		return value != 0 ? of(id, value, condition) : null;
	}

	public static PercentAttribute ofNullable(PercentAttributeId id, Percent value) {
		return ofNullable(id, value, null);
	}

	public static PercentAttribute ofNullable(PercentAttributeId id, Percent value, AttributeCondition condition) {
		return value != null && !value.isZero() ? of(id, value, condition) : null;
	}

	public static BooleanAttribute ofNullable(BooleanAttributeId id, boolean value) {
		return ofNullable(id, value, null);
	}

	public static BooleanAttribute ofNullable(BooleanAttributeId id, boolean value, AttributeCondition condition) {
		return value ? of(id, value, condition) : null;
	}

	public static DurationAttribute ofNullable(DurationAttributeId id, Duration value) {
		return ofNullable(id, value, null);
	}

	public static DurationAttribute ofNullable(DurationAttributeId id, Duration value, AttributeCondition condition) {
		return value != null && !value.isZero() ? of(id, value, condition) : null;
	}

	public abstract AttributeId getId();

	public double getDouble() {
		throw new IllegalArgumentException(getId() + " isn't a double attribute");
	}

	public Percent getPercent() {
		throw new IllegalArgumentException(getId() + " isn't a percent attribute");
	}

	public boolean getBoolean() {
		throw new IllegalArgumentException(getId() + " isn't a boolean attribute");
	}

	public Duration getDuration() {
		throw new IllegalArgumentException(getId() + " isn't a duration attribute");
	}

	public PrimitiveAttribute scale(double factor) {
		throw new IllegalArgumentException("Can't scale " + getId());
	}

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
