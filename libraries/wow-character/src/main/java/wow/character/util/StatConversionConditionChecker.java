package wow.character.util;

import lombok.RequiredArgsConstructor;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.effect.component.StatConversion;
import wow.commons.model.spell.ActionType;

import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;
import static wow.commons.model.spell.ActionType.PHYSICAL;
import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
@RequiredArgsConstructor
public final class StatConversionConditionChecker {
	private final ActionType actionType;
	private final PowerType powerType;

	public boolean check(StatConversion statConversion) {
		return switch (statConversion.type()) {
			case OWNER_INTELLECT_TO_SPELL_POWER, OWNER_SPIRIT_TO_SPELL_POWER 
					
					-> actionType == SPELL;

			case OWNER_INTELLECT_TO_SPELL_DAMAGE, PET_STAMINA_TO_SPELL_DAMAGE, PET_INTELLECT_TO_SPELL_DAMAGE, MASTER_POWER_TO_SPELL_DAMAGE 
					
					-> powerType == SPELL_DAMAGE;

			case OWNER_INTELLECT_TO_SPELL_HEALING 
					
					-> powerType == HEALING;

			case MASTER_POWER_TO_ATTACK_POWER 
					
					-> actionType == PHYSICAL;

			case OWNER_INTELLECT_TO_MP5, OWNER_INTELLECT_TO_ARMOR, MASTER_STAMINA_TO_STAMINA, MASTER_INTELLECT_TO_INTELLECT, MASTER_HIT_PCT_TO_HIT_PCT, MASTER_HIT_RATING_TO_HIT_RATING 
					
					-> true;
		};
	}
}
