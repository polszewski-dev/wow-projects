package wow.commons.model.attributes.complex.special;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.spells.EffectId;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.model.attributes.primitive.PrimitiveAttributeId.CRIT_COEFF_PCT;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
@Getter
public class TalentProcAbility extends SpecialAbility {
	private final ProcEvent event;
	private final EffectId effectId;
	private final Duration duration;
	private final int stacks;

	public TalentProcAbility(
			ProcEvent event,
			EffectId effectId,
			Duration duration,
			int stacks,
			String line,
			AttributeCondition condition,
			SpecialAbilitySource source
	) {
		super(line, 4, condition, source);
		this.event = event;
		this.effectId = effectId;
		this.duration = duration;
		this.stacks = stacks;
	}

	@Override
	public TalentProcAbility attachCondition(AttributeCondition condition) {
		return new TalentProcAbility(event, effectId, duration, stacks, getLine(), condition, getSource());
	}

	@Override
	public TalentProcAbility attachSource(SpecialAbilitySource source) {
		return new TalentProcAbility(event, effectId, duration, stacks, getLine(), condition, source);
	}

	@Override
	public Attributes getStatEquivalent(StatProvider statProvider) {
		double critChance = statProvider.getCritChance();
		double extraCritCoeff = getExtraCritCoeff(critChance);
		if (extraCritCoeff == 0) {
			return Attributes.EMPTY;
		}
		return Attributes.of(CRIT_COEFF_PCT, extraCritCoeff, condition);
	}

	private double getExtraCritCoeff(double critChance) {
		int rank = switch (effectId) {
			case SHADOW_VULNERABILITY_4 -> 1;
			case SHADOW_VULNERABILITY_8 -> 2;
			case SHADOW_VULNERABILITY_12 -> 3;
			case SHADOW_VULNERABILITY_16 -> 4;
			case SHADOW_VULNERABILITY_20 -> 5;
			default -> throw new IllegalArgumentException("Unhandled: " + effectId);
		};
		double c = critChance;
		double n = 1 - c;
		return rank * 0.04 * (2 * c + n) * (1 + n + n * n + n * n * n);
	}

	@Override
	protected String doToString() {
		return Stream.of(
				effectId != null ? "effect: " + effectId : "",
				!duration.isZero() ? "duration: " + duration : "",
				stacks != 0 ? "stacks: " + stacks : ""
		).filter(x -> !x.isEmpty()).collect(Collectors.joining(", ", "(", ")"));
	}
}
