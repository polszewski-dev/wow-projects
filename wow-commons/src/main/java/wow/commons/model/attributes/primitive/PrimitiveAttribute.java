package wow.commons.model.attributes.primitive;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-04
 */
public class PrimitiveAttribute extends Attribute {
	private final PrimitiveAttributeId id;
	private final double value;
	private Percent percentValue;
	private Duration durationValue;

	private PrimitiveAttribute(PrimitiveAttributeId id, double value, AttributeCondition condition, Percent percentValue, Duration durationValue) {
		super(condition);
		this.id = id;
		this.value = value;
		this.percentValue = percentValue;
		this.durationValue = durationValue;
	}

	public PrimitiveAttribute(PrimitiveAttributeId id, double value, AttributeCondition condition) {
		this(id, value, condition, null, null);
	}

	public PrimitiveAttribute(PrimitiveAttributeId id, Percent value, AttributeCondition condition) {
		this(id, value.getValue(), condition, value, null);
	}

	public PrimitiveAttribute(PrimitiveAttributeId id, Duration value, AttributeCondition condition) {
		this(id, value.getSeconds(), condition, null, value);
	}

	@Override
	public PrimitiveAttributeId getId() {
		return id;
	}

	public double getDouble() {
		return value;
	}

	public Percent getPercent() {
		if (percentValue == null) {
			this.percentValue = Percent.of(value);
		}
		return percentValue;
	}

	public Duration getDuration() {
		if (durationValue == null) {
			this.durationValue = Duration.seconds(value);
		}
		return durationValue;
	}

	@Override
	public PrimitiveAttribute attachCondition(AttributeCondition condition) {
		return new PrimitiveAttribute(id, value, condition, percentValue, durationValue);
	}

	public PrimitiveAttribute scale(double factor) {
		double scaledValue = factor * value;
		return new PrimitiveAttribute(id, scaledValue, getCondition());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof PrimitiveAttribute)) return false;
		PrimitiveAttribute that = (PrimitiveAttribute) o;
		return Double.compare(that.value, value) == 0 && id == that.id && Objects.equals(condition, that.condition);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, value, condition);
	}

	@Override
	public String toString() {//TODO format hints
		String idStr = (id.name().endsWith("_PCT") ? "% " : " ") + id;
		if (value % 1 == 0) {
			return String.format("%s%s%s", (int)value, idStr, getConditionString());
		} else {
			return String.format("%.2f%s%s", value, idStr, getConditionString());
		}
	}
}
