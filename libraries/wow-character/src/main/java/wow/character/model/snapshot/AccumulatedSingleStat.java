package wow.character.model.snapshot;

import lombok.Getter;
import wow.character.model.character.Character;
import wow.character.util.AttributeConditionChecker;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.AttributeId;
import wow.commons.model.spell.Spell;

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

	public AccumulatedSingleStat(AttributeId attributeId, Character caster, Spell spell, Character target, AttributeConditionChecker checker) {
		super(caster, spell, target, checker);
		this.attributeId = attributeId;
	}

	public static AccumulatedSingleStat forSpell(AttributeId attributeId, Character caster, Spell spell, Character target) {
		Objects.requireNonNull(caster);
		Objects.requireNonNull(spell);

		var checker = new AttributeConditionChecker(caster, spell, target, SPELL, null, spell.getSchool());

		return new AccumulatedSingleStat(attributeId, caster, spell, target, checker);
	}

	@Override
	public void accumulateAttribute(Attribute attribute, double scaleFactor) {
		if (attribute.id() == attributeId) {
			this.totalValue += getValue(attribute, scaleFactor);
		}
	}
}
