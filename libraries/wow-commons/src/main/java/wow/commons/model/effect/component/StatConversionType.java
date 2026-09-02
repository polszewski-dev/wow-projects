package wow.commons.model.effect.component;

import lombok.Getter;
import wow.commons.model.attribute.AttributeId;
import wow.commons.util.EnumUtil;

import static wow.commons.model.attribute.AttributeId.*;
import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;
import static wow.commons.model.spell.ActionType.PHYSICAL;
import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2026-09-02
 */
@Getter
public enum StatConversionType implements EffectComponent {
	OWNER_INTELLECT_TO_SPELL_POWER("Intellect -> Power [Spell]", POWER, StatConversionCondition.of(SPELL)),
	OWNER_INTELLECT_TO_SPELL_DAMAGE("Intellect -> Power [SpellDamage]", POWER, StatConversionCondition.of(SPELL_DAMAGE)),
	OWNER_INTELLECT_TO_SPELL_HEALING("Intellect -> Power [Healing]", POWER, StatConversionCondition.of(HEALING)),
	OWNER_INTELLECT_TO_MP5("Intellect -> Mp5", MP5, StatConversionCondition.EMPTY),
	OWNER_INTELLECT_TO_ARMOR("Intellect -> Armor", ARMOR, StatConversionCondition.EMPTY),

	OWNER_SPIRIT_TO_SPELL_POWER("Spirit -> Power [Spell]", POWER, StatConversionCondition.of(SPELL)),

	PET_STAMINA_TO_SPELL_DAMAGE("Pet.Stamina -> Power [SpellDamage]", POWER, StatConversionCondition.of(SPELL_DAMAGE)),
	PET_INTELLECT_TO_SPELL_DAMAGE("Pet.Intellect -> Power [SpellDamage]", POWER, StatConversionCondition.of(SPELL_DAMAGE)),

	MASTER_STAMINA_TO_STAMINA("Master.Stamina -> Stamina", STAMINA, StatConversionCondition.EMPTY),
	MASTER_INTELLECT_TO_INTELLECT("Master.Intellect -> Intellect", INTELLECT, StatConversionCondition.EMPTY),
	MASTER_POWER_TO_SPELL_DAMAGE("Master.Power -> Power [SpellDamage]", POWER, StatConversionCondition.of(SPELL_DAMAGE)),
	MASTER_POWER_TO_ATTACK_POWER("Master.Power -> Power [Physical]", POWER, StatConversionCondition.of(PHYSICAL)),
	MASTER_HIT_PCT_TO_HIT_PCT("Master.Hit% -> Hit%", HIT_PCT, StatConversionCondition.EMPTY),
	MASTER_HIT_RATING_TO_HIT_RATING("Master.HitRating -> HitRating", HIT_RATING, StatConversionCondition.EMPTY),
	;

	private final String key;
	private final AttributeId to;
	private final StatConversionCondition toCondition;

	StatConversionType(String key, AttributeId to, StatConversionCondition toCondition) {
		this.key = key;
		this.to = to;
		this.toCondition = toCondition;
	}

	public static StatConversionType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
