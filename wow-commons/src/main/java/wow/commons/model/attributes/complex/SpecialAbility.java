package wow.commons.model.attributes.complex;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.*;
import wow.commons.model.attributes.complex.modifiers.*;
import wow.commons.model.effects.EffectId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-10-15
 */
public final class SpecialAbility extends ComplexAttribute implements ConditionalAttribute, StatEquivalentProvider {
	private final AttributeModifier attributeModifier;
	private final String line;
	private final AttributeCondition condition;

	private SpecialAbility(AttributeModifier attributeModifier, String line) {
		this(attributeModifier, line, null);
	}

	private SpecialAbility(AttributeModifier attributeModifier, String line, AttributeCondition condition) {
		super(ComplexAttributeId.SPECIAL_ABILITIES);
		this.attributeModifier = attributeModifier;
		this.line = line;
		this.condition = condition;
	}

	public static SpecialAbility onUse(Attributes attributes, Duration duration, Duration cooldown, String line) {
		return new SpecialAbility(new TemporaryModifier(attributes, duration, cooldown), line);
	}

	public static SpecialAbility proc(ProcEvent event, Percent chance, Attributes attributes, Duration duration, Duration cooldown, String line) {
		return new SpecialAbility(new ProcTemporaryModifier(event, chance, attributes, duration, cooldown), line);
	}

	public static SpecialAbility talentProc(ProcEvent event, Percent chance, EffectId effectId, Duration duration, int stacks) {
		return new SpecialAbility(new TemporaryEffect(event, chance, effectId, duration, stacks), null);
	}

	public static SpecialAbility equivalent(Attributes attributes, String line) {
		return new SpecialAbility(new AttributeEquivalent(attributes), line);
	}

	public static SpecialAbility misc(String line) {
		return new SpecialAbility(new AttributeEquivalent(Attributes.EMPTY), line);
	}

	@Override
	public Attributes getStatEquivalent(StatProvider statProvider) {
		return attributeModifier.getAveragedAttributes(statProvider);
	}

	public AttributeModifier getAttributeModifier() {
		return attributeModifier;
	}

	public String getLine() {
		return line;
	}

	public int getPriority() {
		return attributeModifier.getPriority();
	}

	@Override
	public AttributeCondition getCondition() {
		return condition;
	}

	@Override
	public SpecialAbility attachCondition(AttributeCondition condition) {
		return new SpecialAbility(attributeModifier, line, condition);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SpecialAbility)) return false;
		SpecialAbility that = (SpecialAbility) o;
		return Objects.equals(line, that.line);
	}

	@Override
	public int hashCode() {
		return Objects.hash(line);
	}

	@Override
	public String toString() {
		return String.format("%s%s", line, getConditionString());
	}
}
