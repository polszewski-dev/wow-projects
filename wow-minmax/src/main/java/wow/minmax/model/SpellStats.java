package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import wow.character.model.character.Character;
import wow.character.model.snapshot.SpellStatistics;

/**
 * User: POlszewski
 * Date: 2022-01-06
 */
@Data
@AllArgsConstructor
public class SpellStats {
	private Character character;
	private SpellStatistics spellStatistics;
	private SpellStatEquivalents statEquivalents;
}
