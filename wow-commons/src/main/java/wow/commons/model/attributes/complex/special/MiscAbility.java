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
public class MiscAbility extends SpecialAbility {
	public MiscAbility(String line, AttributeCondition condition) {
		super(line, 5, condition);
	}

	@Override
	public MiscAbility attachCondition(AttributeCondition condition) {
		return new MiscAbility(getLine(), condition);
	}

	@Override
	public Attributes getStatEquivalent(StatProvider statProvider) {
		return Attributes.EMPTY;
	}

	@Override
	protected String doToString() {
		return getLine();
	}
}
