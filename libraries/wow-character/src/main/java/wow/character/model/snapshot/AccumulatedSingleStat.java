package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.attribute.PowerType;
import wow.commons.model.spell.ActionType;
import wow.commons.model.spell.Spell;
import wow.commons.model.spell.SpellSchool;

import java.util.Objects;

import static wow.commons.model.spell.ActionType.SPELL;

/**
 * User: POlszewski
 * Date: 2026-11-21
 */
public class AccumulatedSingleStat extends AccumulatedPartialCasterStats {
	private final AttributeId attributeId;
	@Getter
	private double totalValue;

	public AccumulatedSingleStat(AttributeId attributeId, Character caster, Spell spell, Character target, ActionType actionType, PowerType powerType, SpellSchool spellSchool) {
		super(caster, spell, target, actionType, powerType, spellSchool);
		this.attributeId = attributeId;
	}

	public static AccumulatedSingleStat forSpell(AttributeId attributeId, Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		return new AccumulatedSingleStat(attributeId, caster, spell, target, SPELL, null, null);
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		if (attribute.id() == attributeId) {
			this.totalValue += getValue(attribute, scaleFactor);
		}
	}
}
