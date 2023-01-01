package wow.minmax.service;

import wow.character.model.build.BuffSetId;
import wow.character.model.character.Character;
import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SpellStatistics;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.spells.Spell;
import wow.minmax.model.CharacterStats;
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

	Attributes getDpsStatEquivalent(Attributes attributesToFindEquivalent, PrimitiveAttributeId targetStat, EquivalentMode mode, Character character, Spell spell, Attributes totalStats);

	Attributes getAbilityEquivalent(SpecialAbility specialAbility, Character character, Spell spell, Attributes totalStats);

	SpellStatistics getSpellStatistics(Character character, Spell spell);

	SpellStatistics getSpellStatistics(Character character, Spell spell, Attributes totalStats);

	Snapshot getSnapshot(Character character, Spell spell, Attributes totalStats);

	SpellStats getSpellStats(Character character, Spell spell);

	CharacterStats getCurrentStats(Character character);

	CharacterStats getStats(Character character, BuffSetId... selfBuffs);

	CharacterStats getEquipmentStats(Character character);

	SpecialAbilityStats getSpecialAbilityStats(Character character, SpecialAbility specialAbility);
}
