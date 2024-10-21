package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.spell.Ability;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
@AllArgsConstructor
@Getter
public class RotationSpellStats {
	private final Ability spell;
	private final double numCasts;
	private final double damage;
}
