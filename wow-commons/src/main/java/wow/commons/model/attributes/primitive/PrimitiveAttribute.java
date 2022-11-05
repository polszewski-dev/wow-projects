package wow.commons.model.attributes.primitive;

import wow.commons.model.attributes.Attribute;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.ConditionalAttribute;

/**
 * User: POlszewski
 * Date: 2022-01-04
 */
public abstract class PrimitiveAttribute extends Attribute implements ConditionalAttribute {
	private final AttributeCondition condition;

	protected PrimitiveAttribute(AttributeId id, AttributeCondition condition) {
		super(id);
		this.condition = condition;
	}

	@Override
	public AttributeCondition getCondition() {
		return condition;
	}
}
