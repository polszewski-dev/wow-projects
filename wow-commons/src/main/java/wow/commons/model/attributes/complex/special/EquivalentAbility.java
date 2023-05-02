package wow.commons.model.attributes.complex.special;

import lombok.Getter;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.SpecialAbilitySource;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
@Getter
public class EquivalentAbility extends SpecialAbility {
	public EquivalentAbility(Attributes attributes, String line, AttributeCondition condition, SpecialAbilitySource source) {
		super(line, 1, attributes, condition, source);
		if (!attributes.getComplexAttributeMap().isEmpty()) {
			throw new IllegalArgumentException("Equivalent can't have complex attributes");
		}
	}

	@Override
	public EquivalentAbility attachCondition(AttributeCondition condition) {
		return new EquivalentAbility(getAttributes(), getLine(), condition, getSource());
	}

	@Override
	public EquivalentAbility attachSource(SpecialAbilitySource source) {
		return new EquivalentAbility(getAttributes(), getLine(), condition, source);
	}

	@Override
	protected String doToString() {
		return getAttributes().toString();
	}
}
