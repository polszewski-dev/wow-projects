package wow.commons.model.attributes.complex;

import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;

import static wow.commons.util.PrimitiveAttributeFormatter.getConditionString;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public abstract class ComplexAttribute extends Attribute {
	private final ComplexAttributeId id;

	protected ComplexAttribute(ComplexAttributeId id, AttributeCondition condition) {
		super(condition);
		this.id = id;
	}

	@Override
	public ComplexAttributeId getId() {
		return id;
	}

	@Override
	public abstract ComplexAttribute attachCondition(AttributeCondition condition);

	@Override
	public final String toString() {
		return doToString() + getConditionString(condition);
	}

	protected abstract String doToString();
}
