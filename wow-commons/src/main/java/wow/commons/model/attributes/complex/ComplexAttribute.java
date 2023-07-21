package wow.commons.model.attributes.complex;

import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
public non-sealed interface ComplexAttribute extends Attribute {
	@Override
	ComplexAttributeId id();

	@Override
	ComplexAttribute attachCondition(AttributeCondition condition);
}
