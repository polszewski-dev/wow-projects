package wow.minmax.model;

import wow.commons.model.pve.Faction;
import wow.commons.model.pve.FactionExclusionGroupId;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-09-29
 */
public record ExclusiveFactionGroup(
		FactionExclusionGroupId groupId,
		Faction selectedFaction,
		List<Faction> availableFactions
) {
}
