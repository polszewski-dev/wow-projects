package wow.commons.model.attribute.complex.special;

import wow.commons.model.Duration;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.condition.AttributeCondition;
import wow.commons.model.effect.EffectId;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.commons.util.PrimitiveAttributeFormatter.getConditionString;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
public record TalentProcAbility(
		ProcEvent event,
		EffectId effectId,
		Duration duration,
		int stacks,
		String line,
		AttributeCondition condition,
		SpecialAbilitySource source
) implements SpecialAbility {
	public TalentProcAbility {
		Objects.requireNonNull(event);
		Objects.requireNonNull(effectId);
		Objects.requireNonNull(duration);
		Objects.requireNonNull(line);
		Objects.requireNonNull(condition);
	}

	@Override
	public int priority() {
		return 4;
	}

	@Override
	public Attributes attributes() {
		return Attributes.EMPTY;
	}

	@Override
	public TalentProcAbility attachCondition(AttributeCondition condition) {
		return new TalentProcAbility(event, effectId, duration, stacks, line, condition, source);
	}

	@Override
	public TalentProcAbility attachSource(SpecialAbilitySource source) {
		return new TalentProcAbility(event, effectId, duration, stacks, line, condition, source);
	}

	@Override
	public String toString() {
		return Stream.of(
				effectId != null ? "effect: " + effectId : "",
				!duration.isZero() ? "duration: " + duration : "",
				stacks != 0 ? "stacks: " + stacks : ""
		).filter(x -> !x.isEmpty()).collect(Collectors.joining(", ", "(", ")")) + getConditionString(condition);
	}
}
