package wow.simulator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.simulator.client.dto.CooldownStatsDTO;
import wow.simulator.model.stats.CooldownStats;
import wow.simulator.model.unit.Player;

/**
 * User: POlszewski
 * Date: 2025-09-23
 */
@Component
@AllArgsConstructor
public class CooldownStatsConverter implements ParametrizedConverter<CooldownStats, CooldownStatsDTO, Player> {
	@Override
	public CooldownStatsDTO doConvert(CooldownStats source, Player player) {
		var spellId = source.getSpellId(player);

		return new CooldownStatsDTO(
				spellId.value(),
				source.getUptime().getSeconds(),
				source.getCount()
		);
	}
}
