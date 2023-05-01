package wow.commons.model.attributes.complex.special;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.SpecialAbilitySource;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
@Getter
public class ProcAbility extends SpecialAbility {
	private final ProcEvent event;
	private final Duration duration;
	private final Duration cooldown;

	public ProcAbility(
			ProcEvent event,
			Attributes attributes,
			Duration duration,
			Duration cooldown,
			String line,
			AttributeCondition condition,
			SpecialAbilitySource source
	) {
		super(line, 3, attributes, condition, source);
		this.event = event;
		this.duration = duration;
		this.cooldown = cooldown != null ? cooldown : Duration.ZERO;
		if (event == null || attributes == null || duration == null) {
			throw new NullPointerException();
		}
	}

	@Override
	public ProcAbility attachCondition(AttributeCondition condition) {
		return new ProcAbility(event, getAttributes(), duration, cooldown, getLine(), condition, getSource());
	}

	@Override
	public ProcAbility attachSource(SpecialAbilitySource source) {
		return new ProcAbility(event, getAttributes(), duration, cooldown, getLine(), condition, source);
	}

	@Override
	public Attributes getStatEquivalent(StatProvider statProvider) {
		double hitChance = statProvider.getHitChance();
		double critChance = statProvider.getCritChance();
		double castTime = statProvider.getEffectiveCastTime();

		double procChance = event.getProcChance(hitChance, critChance);

		if (procChance == 0) {
			return Attributes.EMPTY;
		}

		double theoreticalCooldown = castTime / procChance;
		double actualCooldown = max(cooldown.getSeconds(), theoreticalCooldown);

		double factor = min(duration.getSeconds() / actualCooldown, 1);

		return getAttributes().scale(factor);
	}

	@Override
	protected String doToString() {
		return "(event: %s, chance: %s, %s | %s/%s)".formatted(event.getType(), event.getChance(), getAttributes(), duration, cooldown.isZero() ? "-" : cooldown);
	}
}
