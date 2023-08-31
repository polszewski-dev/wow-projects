package wow.minmax.service;

import wow.character.model.build.Rotation;
import wow.character.model.character.Character;
import wow.character.model.snapshot.Snapshot;
import wow.commons.model.attribute.Attributes;
import wow.commons.model.attribute.complex.special.SpecialAbility;
import wow.commons.model.attribute.primitive.PrimitiveAttributeId;
import wow.commons.model.buff.BuffCategory;
import wow.commons.model.spell.Spell;
import wow.commons.model.talent.TalentId;
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

	Attributes getTalentEquivalent(TalentId talentId, PrimitiveAttributeId targetStat, Character character);

	double getRotationDps(Character character, Rotation rotation);

	double getRotationDps(Character character, Rotation rotation, Attributes totalStats);

	RotationStats getRotationStats(Character character, Rotation rotation);

	Snapshot getSnapshot(Character character, Attributes totalStats);

	Snapshot getSnapshot(Character character, Spell spell, Attributes totalStats);

	Attributes getStatEquivalent(SpecialAbility specialAbility, Snapshot snapshot);

	SpellStats getSpellStats(Character character, Spell spell);

	CharacterStats getCurrentStats(Character character);

	CharacterStats getStats(Character character, BuffCategory... buffCategories);

	CharacterStats getEquipmentStats(Character character);

	SpecialAbilityStats getSpecialAbilityStats(Character character, SpecialAbility specialAbility);
}
