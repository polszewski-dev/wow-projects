package wow.commons.model.attributes.complex.special;

import lombok.Getter;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
@Getter
public class EquivalentAbility extends SpecialAbility {
	private final Attributes attributes;

	public EquivalentAbility(Attributes attributes, String line, AttributeCondition condition, SpecialAbilitySource source) {
		super(line, 1, condition, source);
		this.attributes = attributes;
		if (!attributes.getComplexAttributeMap().isEmpty()) {
			throw new IllegalArgumentException("Equivalent can't have complex attributes");
		}
	}

	@Override
	public EquivalentAbility attachCondition(AttributeCondition condition) {
		return new EquivalentAbility(attributes, getLine(), condition, getSource());
	}

	@Override
	public EquivalentAbility attachSource(SpecialAbilitySource source) {
		return new EquivalentAbility(attributes, getLine(), condition, source);
	}

	@Override
	public Attributes getStatEquivalent(StatProvider statProvider) {
		return AttributesBuilder.attachCondition(attributes, condition);
	}

	@Override
	protected String doToString() {
		return attributes.toString();
	}
}
