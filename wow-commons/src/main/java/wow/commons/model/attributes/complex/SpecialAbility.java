package wow.commons.model.attributes.complex;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatEquivalentProvider;
import wow.commons.model.attributes.complex.special.*;
import wow.commons.model.spells.EffectId;

/**
 * User: POlszewski
 * Date: 2021-10-15
 */
@Getter
public abstract class SpecialAbility extends ComplexAttribute implements StatEquivalentProvider {
	private final String line;
	private final int priority;

	protected SpecialAbility(String line, int priority, AttributeCondition condition) {
		super(ComplexAttributeId.SPECIAL_ABILITIES, condition);
		this.line = line;
		this.priority = priority;
	}

	public static OnUseAbility onUse(Attributes attributes, Duration duration, Duration cooldown, String line) {
		return new OnUseAbility(attributes, duration, cooldown, line, AttributeCondition.EMPTY);
	}

	public static ProcAbility proc(ProcEvent event, Percent chance, Attributes attributes, Duration duration, Duration cooldown, String line) {
		return new ProcAbility(event, chance, attributes, duration, cooldown, line, AttributeCondition.EMPTY);
	}

	public static TalentProcAbility talentProc(ProcEvent event, Percent chance, EffectId effectId, Duration duration, int stacks, String line) {
		return new TalentProcAbility(event, chance, effectId, duration, stacks, line, AttributeCondition.EMPTY);
	}

	public static EquivalentAbility equivalent(Attributes attributes, String line) {
		return new EquivalentAbility(attributes, line, AttributeCondition.EMPTY);
	}

	public static MiscAbility misc(String line) {
		return new MiscAbility(line, AttributeCondition.EMPTY);
	}
}
