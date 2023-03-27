package wow.commons.model.attributes.complex.special;

import lombok.Getter;
import wow.commons.model.Duration;
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
public class OnUseAbility extends SpecialAbility {
	private final Attributes attributes;
	private final Duration duration;
	private final Duration cooldown;

	public OnUseAbility(
			Attributes attributes,
			Duration duration,
			Duration cooldown,
			String line,
			AttributeCondition condition,
			SpecialAbilitySource source
	) {
		super(line, 2, condition, source);
		this.attributes = attributes;
		this.duration = duration;
		this.cooldown = cooldown;
		if (attributes == null || duration == null || cooldown == null) {
			throw new NullPointerException();
		}
	}

	@Override
	public OnUseAbility attachCondition(AttributeCondition condition) {
		return new OnUseAbility(attributes, duration, cooldown, getLine(), condition, getSource());
	}

	@Override
	public OnUseAbility attachSource(SpecialAbilitySource source) {
		return new OnUseAbility(attributes, duration, cooldown, getLine(), condition, source);
	}

	@Override
	public Attributes getStatEquivalent(StatProvider statProvider) {
		double factor = duration.divideBy(cooldown);
		return AttributesBuilder.attachCondition(attributes.scale(factor), condition);
	}

	@Override
	protected String doToString() {
		return String.format("(%s | %s/%s)", attributes, duration, cooldown);
	}
}
