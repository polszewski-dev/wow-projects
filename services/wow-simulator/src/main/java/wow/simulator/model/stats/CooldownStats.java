package wow.simulator.model.stats;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import wow.commons.model.Duration;
import wow.commons.model.spell.CooldownId;

/**
 * User: POlszewski
 * Date: 2024-11-12
 */
@RequiredArgsConstructor
@Getter
public class CooldownStats {
	private final CooldownId cooldownId;
	private Duration uptime = Duration.ZERO;

	public void addUptime(Duration duration) {
		this.uptime = uptime.add(duration);
	}
}
