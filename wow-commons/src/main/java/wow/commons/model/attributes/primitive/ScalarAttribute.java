package wow.commons.model.attributes.primitive;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;

/**
 * User: POlszewski
 * Date: 2022-09-26
 */
public abstract class ScalarAttribute extends PrimitiveAttribute {
	protected ScalarAttribute(AttributeId id, AttributeCondition condition) {
		super(id, condition);
	}
}
