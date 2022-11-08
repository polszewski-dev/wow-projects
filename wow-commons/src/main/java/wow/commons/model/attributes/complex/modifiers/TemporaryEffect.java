package wow.commons.model.attributes.complex.modifiers;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.effects.EffectId;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2021-11-06
 */
public class TemporaryEffect implements AttributeModifier {
	private final ProcEvent event;
	private final Percent chance;
	private final EffectId effectId;
	private final Duration duration;
	private final int stacks;

	public TemporaryEffect(ProcEvent event, Percent chance, EffectId effectId, Duration duration, int stacks) {
		this.event = event;
		this.chance = chance;
		this.effectId = effectId;
		this.duration = duration;
		this.stacks = stacks;
	}

	@Override
	public ProcEvent getEvent() {
		return event;
	}

	@Override
	public Percent getChance() {
		return chance;
	}

	public EffectId getEffectId() {
		return effectId;
	}

	public Duration getDuration() {
		return duration;
	}

	public int getStacks() {
		return stacks;
	}

	@Override
	public Attributes getAveragedAttributes(StatProvider statProvider) {
		double critChance = statProvider.critChance();
		double extraCritCoeff = getExtraCritCoeff(critChance);
		if (extraCritCoeff == 0) {
			return Attributes.EMPTY;
		}
		return Attributes.of(AttributeId.EXTRA_CRIT_COEFF, extraCritCoeff);
	}

	@Override
	public int getPriority() {
		return 3;
	}

	private double getExtraCritCoeff(double critChance) {
		int rank;
		switch (effectId) {
			case SHADOW_VULNERABILITY_4:
				rank = 1;
				break;
			case SHADOW_VULNERABILITY_8:
				rank = 2;
				break;
			case SHADOW_VULNERABILITY_12:
				rank = 3;
				break;
			case SHADOW_VULNERABILITY_16:
				rank = 4;
				break;
			case SHADOW_VULNERABILITY_20:
				rank = 5;
				break;
			default:
				throw new IllegalArgumentException("Unhandled: " + effectId);
		}
		double c = critChance, n = 1 - c;
		return rank * 0.04 * (2 * c + n) * (1 + n + n * n + n * n * n);
	}

	@Override
	public String toString() {
		return Stream.of(
				effectId != null ? "effect: " + effectId : "",
				!duration.isZero() ? "duration: " + duration : "",
				stacks != 0 ? "stacks: " + stacks : ""
		).filter(x -> !x.isEmpty()).collect(Collectors.joining(", ", "(", ")"));
	}
}
