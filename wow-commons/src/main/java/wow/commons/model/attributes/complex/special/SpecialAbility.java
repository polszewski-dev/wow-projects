package wow.commons.model.attributes.complex.special;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.ComplexAttribute;
import wow.commons.model.attributes.complex.ComplexAttributeId;
import wow.commons.model.attributes.condition.AttributeCondition;
import wow.commons.model.spells.EffectId;

/**
 * User: POlszewski
 * Date: 2021-10-15
 */
public sealed interface SpecialAbility extends ComplexAttribute
		permits OnUseAbility, ProcAbility, TalentProcAbility, EquivalentAbility, MiscAbility {

	@Override
	default ComplexAttributeId id() {
		return ComplexAttributeId.SPECIAL_ABILITIES;
	}

	String line();

	int priority();

	Attributes attributes();

	SpecialAbilitySource source();

	SpecialAbility attachSource(SpecialAbilitySource source);

	static OnUseAbility onUse(Attributes attributes, Duration duration, Duration cooldown, String line) {
		return new OnUseAbility(attributes, duration, cooldown, line, AttributeCondition.EMPTY, null, null);
	}

	static ProcAbility proc(ProcEventType event, Percent chance, Attributes attributes, Duration duration, Duration cooldown, String line) {
		return new ProcAbility(new ProcEvent(event, chance), attributes, duration, cooldown, line, AttributeCondition.EMPTY, null);
	}

	static TalentProcAbility talentProc(ProcEventType event, Percent chance, EffectId effectId, Duration duration, int stacks, String line) {
		return new TalentProcAbility(new ProcEvent(event, chance), effectId, duration, stacks, line, AttributeCondition.EMPTY, null);
	}

	static EquivalentAbility equivalent(Attributes attributes, String line) {
		return new EquivalentAbility(attributes, line, AttributeCondition.EMPTY, null);
	}

	static MiscAbility misc(String line) {
		return new MiscAbility(line, AttributeCondition.EMPTY, null);
	}
}
