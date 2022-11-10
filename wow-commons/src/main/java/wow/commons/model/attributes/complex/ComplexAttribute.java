package wow.commons.model.attributes.complex;

import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;

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
}
