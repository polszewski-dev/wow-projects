package wow.minmax.service;

import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SpellStatistics;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.model.spells.Spell;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerSpellStats;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface CalculationService {
	enum EquivalentMode {
		ADDITIONAL,
		REPLACEMENT,
	}

	Attributes getDpsStatEquivalent(Attributes attributesToFindEquivalent, PrimitiveAttributeId targetStat, EquivalentMode mode, PlayerProfile playerProfile);

	Attributes getDpsStatEquivalent(Attributes attributesToFindEquivalent, PrimitiveAttributeId targetStat, EquivalentMode mode, PlayerProfile playerProfile, Spell spell, Attributes totalStats);

	Attributes getAbilityEquivalent(SpecialAbility specialAbility, PlayerProfile playerProfile, Spell spell, Attributes totalStats);

	SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell);

	SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell, Attributes totalStats);

	Snapshot getSnapshot(PlayerProfile playerProfile, Spell spell, Attributes totalStats);

	PlayerSpellStats getPlayerSpellStats(PlayerProfile playerProfile, Spell spell);
}
