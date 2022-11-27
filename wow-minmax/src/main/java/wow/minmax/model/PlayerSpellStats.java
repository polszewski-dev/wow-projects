package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.commons.model.spells.SpellStatistics;

/**
 * User: POlszewski
 * Date: 2022-01-06
 */
@Data
@AllArgsConstructor
public class PlayerSpellStats {
	private PlayerProfile playerProfile;
	private SpellStatistics spellStatistics;
	private double hitSpEqv;
	private double critSpEqv;
	private double hasteSpEqv;
}
