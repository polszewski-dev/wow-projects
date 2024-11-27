package wow.character.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class AttributeConditionArgs {
	private final Character caster;
	private final Spell spell;
	private final Character target;

	private final ActionType actionType;
	private PowerType powerType;
	private WeaponSubType weaponType;

	private boolean canCrit;
	private boolean hadCrit;

	private boolean direct;
	private boolean periodic;

	private SpellSchool spellSchool;

	private boolean hostileSpell;
	private boolean normalMeleeAttack;
	private boolean specialAttack;

	public static AttributeConditionArgs forAnySpell(Character caster) {
		Objects.requireNonNull(caster);
		return new AttributeConditionArgs(caster, null, null, ActionType.SPELL);
	}

	public static AttributeConditionArgs forSpell(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);
		return new AttributeConditionArgs(caster, spell, target, ActionType.SPELL);
	}

	public static AttributeConditionArgs forSpellTarget(Character target, Spell spell) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);
		return new AttributeConditionArgs(target, spell, null, ActionType.SPELL);
	}

	public static AttributeConditionArgs forBaseStats(Character caster) {
		Objects.requireNonNull(caster);
		return new AttributeConditionArgs(caster, null, null, null);
	}

	public SpellSchool getSpellSchool() {
		if (spellSchool != null) {
			return spellSchool;
		}
		if (spell != null) {
			return spell.getSchool();
		}
		return null;
	}
}
