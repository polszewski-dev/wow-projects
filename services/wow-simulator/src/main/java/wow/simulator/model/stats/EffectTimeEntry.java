package wow.simulator.model.stats;

import lombok.Getter;
import wow.commons.model.spell.AbilityId;
import wow.simulator.model.time.Time;

/**
 * User: POlszewski
 * Date: 2024-11-12
 */
@Getter
public final class EffectTimeEntry extends TimeEntry {
	private final AbilityId abilityId;

	public EffectTimeEntry(AbilityId abilityId, Time begin) {
		super(begin);
		this.abilityId = abilityId;
	}
}
