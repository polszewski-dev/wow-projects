package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.util.SpellStatistics;

/**
 * User: POlszewski
 * Date: 2022-01-06
 */
@Data
@AllArgsConstructor
public class PlayerSpellStats {
	private PlayerProfile playerProfile;
	private SpellStatistics spellStatistics;
}
