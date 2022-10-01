package wow.minmax.service;

import wow.commons.model.attributes.AttributeId;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.StatProvider;
import wow.commons.util.AttributeEvaluator;
import wow.commons.util.Snapshot;
import wow.commons.util.SpellStatistics;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.Spell;

/**
 * User: POlszewski
 * Date: 2021-12-15
 */
public interface CalculationService {
	double getSpEquivalent(AttributeId attributeId, int amount, PlayerProfile playerProfile, Spell spell);

	SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell);

	SpellStatistics getSpellStatistics(PlayerProfile playerProfile, Spell spell, Attributes totalStats);

	Snapshot getSnapshot(PlayerProfile playerProfile, Spell spell, Attributes totalStats);

	StatProvider getPlayerStatsProvider(PlayerProfile playerProfile, Spell spell, AttributeEvaluator attributeEvaluator);
}
