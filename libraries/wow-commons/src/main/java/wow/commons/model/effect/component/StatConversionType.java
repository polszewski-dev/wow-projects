package wow.commons.model.effect.component;

import lombok.Getter;
import wow.commons.model.attribute.AttributeId;
import wow.commons.util.EnumUtil;

import static wow.commons.model.attribute.AttributeId.*;

/**
 * User: POlszewski
 * Date: 2026-09-02
 */
@Getter
public enum StatConversionType implements EffectComponent {
	OWNER_INTELLECT_TO_SPELL_POWER("Intellect -> Power [Spell]", POWER),
	OWNER_INTELLECT_TO_SPELL_DAMAGE("Intellect -> Power [SpellDamage]", POWER),
	OWNER_INTELLECT_TO_SPELL_HEALING("Intellect -> Power [Healing]", POWER),
	OWNER_INTELLECT_TO_MP5("Intellect -> Mp5", MP5),
	OWNER_INTELLECT_TO_ARMOR("Intellect -> Armor", ARMOR),

	OWNER_SPIRIT_TO_SPELL_POWER("Spirit -> Power [Spell]", POWER),

	PET_STAMINA_TO_SPELL_DAMAGE("Pet.Stamina -> Power [SpellDamage]", POWER),
	PET_INTELLECT_TO_SPELL_DAMAGE("Pet.Intellect -> Power [SpellDamage]", POWER),

	MASTER_STAMINA_TO_STAMINA("Master.Stamina -> Stamina", STAMINA),
	MASTER_INTELLECT_TO_INTELLECT("Master.Intellect -> Intellect", INTELLECT),
	MASTER_POWER_TO_SPELL_DAMAGE("Master.Power -> Power [SpellDamage]", POWER),
	MASTER_POWER_TO_ATTACK_POWER("Master.Power -> Power [Physical]", POWER),
	MASTER_HIT_PCT_TO_HIT_PCT("Master.Hit% -> Hit%", HIT_PCT),
	MASTER_HIT_RATING_TO_HIT_RATING("Master.HitRating -> HitRating", HIT_RATING),
	;

	private final String key;
	private final AttributeId to;

	StatConversionType(String key, AttributeId to) {
		this.key = key;
		this.to = to;
	}

	public static StatConversionType parse(String value) {
		return EnumUtil.parse(value, values(), x -> x.key);
	}

	@Override
	public String toString() {
		return key;
	}
}
