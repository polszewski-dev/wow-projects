package wow.commons.model.attributes.complex;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatEquivalentProvider;
import wow.commons.model.attributes.complex.special.*;
import wow.commons.model.spells.EffectId;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2021-10-15
 */
@Getter
public abstract class SpecialAbility extends ComplexAttribute implements StatEquivalentProvider {
	private final String line;
	private final int priority;
	private final Attributes attributes;
	private final SpecialAbilitySource source;

	protected SpecialAbility(String line, int priority, Attributes attributes, AttributeCondition condition, SpecialAbilitySource source) {
		super(ComplexAttributeId.SPECIAL_ABILITIES, condition);
		this.line = line;
		this.priority = priority;
		this.attributes = AttributesBuilder.attachCondition(attributes, condition);
		this.source = source;
		if (this.line == null) {
			throw new IllegalArgumentException();
		}
	}

	public abstract SpecialAbility attachSource(SpecialAbilitySource source);

	public static OnUseAbility onUse(Attributes attributes, Duration duration, Duration cooldown, String line) {
		return new OnUseAbility(attributes, duration, cooldown, line, AttributeCondition.EMPTY, null);
	}

	public static ProcAbility proc(ProcEventType event, Percent chance, Attributes attributes, Duration duration, Duration cooldown, String line) {
		return new ProcAbility(new ProcEvent(event, chance), attributes, duration, cooldown, line, AttributeCondition.EMPTY, null);
	}

	public static TalentProcAbility talentProc(ProcEventType event, Percent chance, EffectId effectId, Duration duration, int stacks, String line) {
		return new TalentProcAbility(new ProcEvent(event, chance), effectId, duration, stacks, line, AttributeCondition.EMPTY, null);
	}

	public static EquivalentAbility equivalent(Attributes attributes, String line) {
		return new EquivalentAbility(attributes, line, AttributeCondition.EMPTY, null);
	}

	public static MiscAbility misc(String line) {
		return new MiscAbility(line, AttributeCondition.EMPTY, null);
	}
}
