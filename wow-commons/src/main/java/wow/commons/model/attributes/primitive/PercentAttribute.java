package wow.commons.model.attributes.primitive;

import wow.commons.model.Percent;
import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public final class PercentAttribute extends ScalarAttribute {
	private final Percent value;

	public PercentAttribute(AttributeId id, Percent value, AttributeCondition condition) {
		super(id, condition);
		this.value = value;
		if (!id.isPercentAttribute()) {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Percent getPercent() {
		return value;
	}

	@Override
	public Attribute attachCondition(AttributeCondition condition) {
		return new PercentAttribute(id, value, condition);
	}

	@Override
	public PercentAttribute scale(double factor) {
		Percent value = this.value.scale(factor);
		return new PercentAttribute(id, value, condition);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		PercentAttribute that = (PercentAttribute) o;
		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	protected String getValueString(String idFmt) {
		return String.format("%s" + idFmt, value, id);
	}
}
