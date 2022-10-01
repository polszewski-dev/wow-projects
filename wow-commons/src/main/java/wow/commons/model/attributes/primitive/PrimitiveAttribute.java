package wow.commons.model.attributes.primitive;

import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;

/**
 * User: POlszewski
 * Date: 2022-01-04
 */
public abstract class PrimitiveAttribute extends Attribute {
	protected PrimitiveAttribute(AttributeId id, AttributeCondition condition) {
		super(id, condition);
	}
}
