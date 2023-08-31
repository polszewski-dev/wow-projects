package wow.commons.model.attribute.complex;

import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.condition.AttributeCondition;

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
