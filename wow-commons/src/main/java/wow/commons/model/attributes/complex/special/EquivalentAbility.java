package wow.commons.model.attributes.complex.special;

import lombok.Getter;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
@Getter
public class EquivalentAbility extends SpecialAbility {
	private final Attributes attributes;

	public EquivalentAbility(Attributes attributes, String line, AttributeCondition condition) {
		super(line, 1, condition);
		this.attributes = attributes;
	}

	@Override
	public EquivalentAbility attachCondition(AttributeCondition condition) {
		return new EquivalentAbility(attributes, getLine(), condition);
	}

	@Override
	public Attributes getStatEquivalent(StatProvider statProvider) {
		return attributes;
	}

	@Override
	public String toString() {
		return String.format("%s%s", attributes, getConditionString());
	}
}
