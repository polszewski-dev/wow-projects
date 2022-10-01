package wow.commons.model.attributes.primitive;

import wow.commons.model.Duration;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public final class DurationAttribute extends ScalarAttribute {
	private final Duration value;

	public DurationAttribute(AttributeId id, Duration value, AttributeCondition condition) {
		super(id, condition);
		this.value = value;
		if (!id.isDurationAttribute() || value == null) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Duration getDuration() {
		return value;
	}

	@Override
	public Attribute attachCondition(AttributeCondition condition) {
		return new DurationAttribute(id, value, condition);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof DurationAttribute)) return false;
		DurationAttribute that = (DurationAttribute) o;
		return getId() == that.getId() && value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(getId(), value);
	}

	@Override
	protected String getValueString(String idFmt) {
		return String.format("%s" + idFmt, value, id);
	}
}
