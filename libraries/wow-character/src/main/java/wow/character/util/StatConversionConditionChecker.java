package wow.character.util;

import wow.commons.model.effect.component.StatConversion;

import static wow.commons.model.attribute.PowerType.HEALING;
import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;
import static wow.commons.model.spell.ActionType.PHYSICAL;
import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2025-10-06
 */
public final class StatConversionConditionChecker {
	public static boolean check(StatConversion statConversion, AttributeConditionArgs args) {
		return switch (statConversion.type()) {
			case OWNER_INTELLECT_TO_SPELL_POWER, OWNER_SPIRIT_TO_SPELL_POWER 
					
					-> args.getActionType() == SPELL;

			case OWNER_INTELLECT_TO_SPELL_DAMAGE, PET_STAMINA_TO_SPELL_DAMAGE, PET_INTELLECT_TO_SPELL_DAMAGE, MASTER_POWER_TO_SPELL_DAMAGE 
					
					-> args.getPowerType() == SPELL_DAMAGE;

			case OWNER_INTELLECT_TO_SPELL_HEALING 
					
					-> args.getPowerType() == HEALING;

			case MASTER_POWER_TO_ATTACK_POWER 
					
					-> args.getActionType() == PHYSICAL;

			case OWNER_INTELLECT_TO_MP5, OWNER_INTELLECT_TO_ARMOR, MASTER_STAMINA_TO_STAMINA, MASTER_INTELLECT_TO_INTELLECT, MASTER_HIT_PCT_TO_HIT_PCT, MASTER_HIT_RATING_TO_HIT_RATING 
					
					-> true;
		};
	}

	private StatConversionConditionChecker() {}
}
