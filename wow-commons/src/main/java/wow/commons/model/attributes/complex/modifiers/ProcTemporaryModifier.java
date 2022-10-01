package wow.commons.model.attributes.complex.modifiers;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;

/**
 * User: POlszewski
 * Date: 2022-01-13
 */
public class ProcTemporaryModifier implements AttributeModifier {
	private final ProcEvent event;
	private final Percent chance;
	private final Attributes attributes;
	private final Duration duration;
	private final Duration cooldown;

	public ProcTemporaryModifier(ProcEvent event, Percent chance, Attributes attributes, Duration duration, Duration cooldown) {
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
	public ProcEvent getEvent() {
		return event;
	}

	@Override
	public Percent getChance() {
		return chance;
	}

	@Override
	public Attributes getAveragedAttributes(StatProvider statProvider) {
		Duration castTime = statProvider.castTime();
		double hitChance = statProvider.hitChance();
		double critChance = statProvider.critChance();
		Duration internalCooldown = castTime.divideBy(getProcChance(hitChance, critChance));
		Duration actualCooldown = getActualCooldown(internalCooldown);
		double factor = duration.divideBy(actualCooldown);
		return attributes.scale(factor);
	}

	@Override
	public int getPriority() {
		return 4;
	}

	private double getProcChance(double hitChance, double critChance) {
		double procChance;
		if (event == ProcEvent.SPELL_RESIST) {
			procChance = 1 - hitChance;
		} else if (event == ProcEvent.SPELL_CRIT) {
			procChance = critChance;
		} else {
			procChance = 1;
		}
		return procChance * chance.getCoefficient();
	}

	private Duration getActualCooldown(Duration internalCooldown) {
		if (internalCooldown.isZero()) {
			return this.cooldown;
		} else if (cooldown == null) {
			return internalCooldown;
		} else {
			return cooldown.max(internalCooldown);
		}
	}

	@Override
	public String toString() {
		return String.format("(event: %s, chance: %s, %s | %s/%s)", event, chance, attributes, duration, (cooldown != null ? cooldown : "-"));
	}
}
