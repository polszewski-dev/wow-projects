package wow.minmax.service;

import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.util.Snapshot;
import wow.commons.util.SpellStatistics;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerSpellStats;
import wow.minmax.model.Spell;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface CalculationService {
	Attributes getStatEquivalent(SpecialAbility specialAbility, PlayerProfile playerProfile, Attributes totalStats);

	double getSpEquivalent(PrimitiveAttributeId attributeId, int amount, PlayerProfile playerProfile, Spell spell);

	SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell);

	SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell, Attributes totalStats);

	Snapshot getSnapshot(PlayerProfile playerProfile, Spell spell, Attributes totalStats);

	PlayerSpellStats getPlayerSpellStats(PlayerProfile playerProfile, Spell spell);
}
