package wow.commons.model.attributes.complex.special;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.effects.EffectId;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.EXTRA_CRIT_COEFF;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
@Getter
public class TalentProcAbility extends SpecialAbility {
	private final ProcEvent event;
	private final Percent chance;
	private final EffectId effectId;
	private final Duration duration;
	private final int stacks;

	public TalentProcAbility(ProcEvent event, Percent chance, EffectId effectId, Duration duration, int stacks, String line, AttributeCondition condition) {
		super(line, 4, condition);
		this.event = event;
		this.chance = chance;
		this.effectId = effectId;
		this.duration = duration;
		this.stacks = stacks;
	}

	@Override
	public TalentProcAbility attachCondition(AttributeCondition condition) {
		return new TalentProcAbility(event, chance, effectId, duration, stacks, getLine(), condition);
	}

	@Override
	public Attributes getStatEquivalent(StatProvider statProvider) {
		double critChance = statProvider.getCritChance();
		double extraCritCoeff = getExtraCritCoeff(critChance);
		if (extraCritCoeff == 0) {
			return Attributes.EMPTY;
		}
		return Attributes.of(EXTRA_CRIT_COEFF, extraCritCoeff, condition);
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
		double c = critChance;
		double n = 1 - c;
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
