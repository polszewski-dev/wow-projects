package wow.simulator.model.stats;

import lombok.Getter;
import wow.commons.model.spell.Ability;
import wow.simulator.model.time.Time;

/**
 * User: POlszewski
 * Date: 2024-11-12
 */
@Getter
public final class AbilityTimeEntry extends TimeEntry {
	private final Ability ability;

	public AbilityTimeEntry(Ability ability, Time begin) {
		super(begin);
		this.ability = ability;
	}
}
