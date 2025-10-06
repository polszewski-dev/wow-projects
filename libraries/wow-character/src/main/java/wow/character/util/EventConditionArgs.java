package wow.character.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.PowerType;
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
public class EventConditionArgs {
	private final Character caster;
	private final Spell spell;
	private final Character target;

	private final ActionType actionType;
	private PowerType powerType;

	private boolean canCrit;
	private boolean hadCrit;

	private boolean direct;
	private boolean periodic;

	private SpellSchool spellSchool;

	private boolean hostileSpell;
	private boolean normalMeleeAttack;
	private boolean specialAttack;

	public static EventConditionArgs forSpell(Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);
		return new EventConditionArgs(caster, spell, target, ActionType.SPELL);
	}

	public static EventConditionArgs forSpellTarget(Character target, Spell spell) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);
		return new EventConditionArgs(target, spell, null, ActionType.SPELL);
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
