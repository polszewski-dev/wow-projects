package wow.commons.util;

import lombok.Data;
import wow.commons.model.Duration;

/**
 * User: POlszewski
 * Date: 2021-10-14
 */
@Data
public class SpellStatistics {
	private Snapshot snapshot;
	private double totalDamage;
	private double dps;
	private Duration castTime;
	private double manaCost;
	private double dpm;
}
