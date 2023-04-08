package wow.commons.model.attributes.complex.special;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.util.AttributesBuilder;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
@Getter
public class ProcAbility extends SpecialAbility {
	private final ProcEvent event;
	private final Percent chance;
	private final Attributes attributes;
	private final Duration duration;
	private final Duration cooldown;

	public ProcAbility(
			ProcEvent event,
			Percent chance,
			Attributes attributes,
			Duration duration,
			Duration cooldown,
			String line,
			AttributeCondition condition,
			SpecialAbilitySource source
	) {
		super(line, 3, condition, source);
		this.event = event;
		this.chance = chance;
		this.attributes = attributes;
		this.duration = duration;
		this.cooldown = cooldown;
		if (event == null || chance == null || attributes == null || duration == null) {
			throw new NullPointerException();
		}
	}

	@Override
	public ProcAbility attachCondition(AttributeCondition condition) {
		return new ProcAbility(event, chance, attributes, duration, cooldown, getLine(), condition, getSource());
	}

	@Override
	public ProcAbility attachSource(SpecialAbilitySource source) {
		return new ProcAbility(event, chance, attributes, duration, cooldown, getLine(), condition, source);
	}

	@Override
	public Attributes getStatEquivalent(StatProvider statProvider) {
		double hitChance = statProvider.getHitChance();
		double critChance = statProvider.getCritChance();
		double castTime = statProvider.getEffectiveCastTime();

		double procChance = getProcChance(hitChance, critChance);

		if (procChance == 0) {
			return Attributes.EMPTY;
		}

		double theoreticalCooldown = castTime / (procChance * chance.getCoefficient());
		double actualCooldown = cooldown != null ? max(cooldown.getSeconds(), theoreticalCooldown) : theoreticalCooldown;

		double factor = min(duration.getSeconds() / actualCooldown, 1);

		return AttributesBuilder.attachCondition(attributes.scale(factor), condition);
	}

	private double getProcChance(double hitChance, double critChance) {
		if (event == ProcEvent.SPELL_HIT) {
			return hitChance;
		} else if (event == ProcEvent.SPELL_CRIT) {
			return critChance;
		} else if (event == ProcEvent.SPELL_RESIST) {
			return 1 - hitChance;
		} else if (event == ProcEvent.SPELL_DAMAGE) {
			return 1;
		} else {
			throw new IllegalArgumentException("Unhandled proc event: " + event);
		}
	}

	@Override
	protected String doToString() {
		return String.format("(event: %s, chance: %s, %s | %s/%s)", event, chance, attributes, duration, (cooldown != null ? cooldown : "-"));
	}
}
