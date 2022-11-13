package wow.commons.model.attributes.condition;

import lombok.EqualsAndHashCode;
import wow.commons.model.attributes.AttributeCondition;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
@EqualsAndHashCode
public class EmptyCondition implements AttributeCondition {
	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public String toString() {
		return "";
	}
}
