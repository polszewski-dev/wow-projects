package wow.minmax.service;

import wow.character.model.build.BuffSetId;
import wow.character.model.build.Rotation;
import wow.character.model.character.Character;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.spells.Spell;
import wow.minmax.model.CharacterStats;
import wow.minmax.model.RotationStats;
import wow.minmax.model.SpecialAbilityStats;
import wow.minmax.model.SpellStats;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface CalculationService {
	enum EquivalentMode {
		ADDITIONAL,
		REPLACEMENT,
	}

	Attributes getDpsStatEquivalent(Attributes attributesToFindEquivalent, PrimitiveAttributeId targetStat, EquivalentMode mode, Character character);

	Attributes getDpsStatEquivalent(Attributes attributesToFindEquivalent, PrimitiveAttributeId targetStat, EquivalentMode mode, Character character, Rotation rotation, Attributes totalStats);

	Attributes getAbilityEquivalent(SpecialAbility specialAbility, Character character);

	Attributes getAbilityEquivalent(SpecialAbility specialAbility, Character character, Rotation rotation, Attributes totalStats);

	double getRotationDps(Character character, Rotation rotation);

	double getRotationDps(Character character, Rotation rotation, Attributes totalStats);

	RotationStats getRotationStats(Character character, Rotation rotation);

	SpellStats getSpellStats(Character character, Spell spell);

	CharacterStats getCurrentStats(Character character);

	CharacterStats getStats(Character character, BuffSetId... selfBuffs);

	CharacterStats getEquipmentStats(Character character);

	SpecialAbilityStats getSpecialAbilityStats(Character character, SpecialAbility specialAbility);
}
