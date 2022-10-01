package wow.commons.model.attributes.complex.modifiers;

import wow.commons.model.Duration;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;

/**
 * User: POlszewski
 * Date: 2022-01-13
 */
public class TemporaryModifier implements AttributeModifier {
	private final Attributes attributes;
	private final Duration duration;
	private final Duration cooldown;

	public TemporaryModifier(Attributes attributes, Duration duration, Duration cooldown) {
		this.attributes = attributes;
		this.duration = duration;
		this.cooldown = cooldown;
		if (attributes == null || duration == null || cooldown == null) {
			throw new NullPointerException();
		}
	}

	@Override
	public Attributes getAveragedAttributes(StatProvider statProvider) {
		double factor = duration.divideBy(cooldown);
		return attributes.scale(factor);
	}

	@Override
	public int getPriority() {
		return 2;
	}

	@Override
	public String toString() {
		return String.format("(%s | %s/%s)", attributes, duration, cooldown);
	}
}
