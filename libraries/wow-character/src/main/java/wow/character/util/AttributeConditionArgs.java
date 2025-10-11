package wow.character.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.AttributeScalingParams;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.categorization.WeaponSubType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;
import wow.commons.model.talent.TalentTree;

import java.util.Objects;

import static wow.commons.model.attribute.PowerType.SPELL_DAMAGE;
import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2023-10-13
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Setter
public class AttributeConditionArgs implements AttributeScalingParams {
	private final Character caster;
	private final Spell spell;
	private final Character target;

	private final ActionType actionType;
	private final PowerType powerType;
	private final SpellSchool spellSchool;

	private boolean direct;

	private WeaponSubType weaponType;
	private boolean hadCrit;

	public static AttributeConditionArgs forAnySpell(Character caster) {
		return forAnySpell(caster, null, null);
	}

	public static AttributeConditionArgs forAnySpell(Character caster, PowerType powerType, SpellSchool spellSchool) {
		Objects.requireNonNull(caster);

		return new AttributeConditionArgs(caster, null, null, SPELL, powerType, spellSchool);
	}

	public static AttributeConditionArgs forSpell(Character caster, Spell spell, Character target) {
		return forSpell(caster, spell, target, null, null);
	}

	public static AttributeConditionArgs forSpell(Character caster, Spell spell, Character target, PowerType powerType, SpellSchool spellSchool) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		return new AttributeConditionArgs(caster, spell, target, SPELL, powerType, spellSchool);
	}

	public static AttributeConditionArgs forSpellDamage(Character caster, Spell spell, Character target, SpellSchool spellSchool) {
		return forSpell(caster, spell, target, SPELL_DAMAGE, spellSchool);
	}

	public static AttributeConditionArgs forSpellTarget(Character target, Spell spell, PowerType powerType, SpellSchool spellSchool) {
		Objects.requireNonNull(target);
		Objects.requireNonNull(spell);

		return new AttributeConditionArgs(target, spell, null, SPELL, powerType, spellSchool);
	}

	public static AttributeConditionArgs forBaseStats(Character owner) {
		Objects.requireNonNull(owner);

		return new AttributeConditionArgs(owner, null, null, null, null, null);
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

	@Override
	public int getLevel() {
		return caster.getLevel();
	}

	@Override
	public int getNumberOfEffectsOnTarget(TalentTree tree) {
		if (target == null) {
			return 0;
		}
		return target.getNumberOfEffects(tree);
	}
}
