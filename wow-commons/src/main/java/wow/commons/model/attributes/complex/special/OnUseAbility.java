package wow.commons.model.attributes.complex.special;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.SpecialAbilitySource;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
@Getter
public class OnUseAbility extends SpecialAbility {
	private final Duration duration;
	private final Duration cooldown;
	private final Attributes equivalent;

	public OnUseAbility(
			Attributes attributes,
			Duration duration,
			Duration cooldown,
			String line,
			AttributeCondition condition,
			SpecialAbilitySource source
	) {
		super(line, 2, attributes, condition, source);
		this.duration = duration;
		this.cooldown = cooldown;
		double factor = duration.divideBy(cooldown);
		this.equivalent = getAttributes().scale(factor);
	}

	@Override
	public OnUseAbility attachCondition(AttributeCondition condition) {
		return new OnUseAbility(getAttributes(), duration, cooldown, getLine(), condition, getSource());
	}

	@Override
	public OnUseAbility attachSource(SpecialAbilitySource source) {
		return new OnUseAbility(getAttributes(), duration, cooldown, getLine(), condition, source);
	}

	@Override
	protected String doToString() {
		return "(%s | %s/%s)".formatted(getAttributes(), duration, cooldown);
	}
}
