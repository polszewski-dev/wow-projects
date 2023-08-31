package wow.commons.model.attribute.condition;

/**
 * User: POlszewski
 * Date: 2022-11-11
 */
public record EmptyCondition() implements AttributeCondition {
	@Override
	public boolean isEmpty() {
		return true;
	}

	@Override
	public String getConditionString() {
		return "";
	}

	@Override
	public String toString() {
		return "";
	}
}
