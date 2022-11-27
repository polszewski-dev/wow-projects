package wow.commons.model.attributes.complex.special;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
@Getter
public class OnUseAbility extends SpecialAbility {
	private final Attributes attributes;
	private final Duration duration;
	private final Duration cooldown;

	public OnUseAbility(Attributes attributes, Duration duration, Duration cooldown, String line, AttributeCondition condition) {
		super(line, 2, condition);
		this.attributes = attributes;
		this.duration = duration;
		this.cooldown = cooldown;
		if (attributes == null || duration == null || cooldown == null) {
			throw new NullPointerException();
		}
	}

	@Override
	public OnUseAbility attachCondition(AttributeCondition condition) {
		return new OnUseAbility(attributes, duration, cooldown, getLine(), condition);
	}

	@Override
	public Attributes getStatEquivalent(StatProvider statProvider) {
		double factor = duration.divideBy(cooldown);
		return attributes.scale(factor);
	}

	@Override
	public String toString() {
		return String.format("(%s | %s/%s)", attributes, duration, cooldown);
	}
}
