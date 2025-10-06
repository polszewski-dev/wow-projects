package wow.character.util;

import wow.character.model.character.Character;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.Spell;

/**
 * User: POlszewski
 * Date: 2024-04-22
 */
public final class AttributeConditionArgsUtil {
	public static AttributeConditionArgs getDamagingComponentConditionArgs(Character character, Spell spell, Character target) {
		var conditionArgs = AttributeConditionArgs.forSpell(character, spell, target);

		conditionArgs.setPowerType(PowerType.SPELL_DAMAGE);
		return conditionArgs;
	}

	public static AttributeConditionArgs getDirectComponentConditionArgs(Character character, Spell spell, Character target) {
		var conditionArgs = getDamagingComponentConditionArgs(character, spell, target);

		conditionArgs.setDirect(true);
		return conditionArgs;
	}

	public static AttributeConditionArgs getPeriodicComponentConditionArgs(Character character, Spell spell, Character target) {
		return getDamagingComponentConditionArgs(character, spell, target);
	}

	public static AttributeConditionArgs getTargetConditionArgs(Character target, Spell spell, PowerType powerType) {
		var conditionArgs = AttributeConditionArgs.forSpellTarget(target, spell);

		conditionArgs.setPowerType(powerType);
		return conditionArgs;
	}

	private AttributeConditionArgsUtil() {}
}
