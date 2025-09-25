package wow.simulator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.simulator.client.dto.EffectStatsDTO;
import wow.simulator.model.stats.EffectStats;

/**
 * User: POlszewski
 * Date: 2025-09-23
 */
@Component
@AllArgsConstructor
public class EffectStatsConverter implements Converter<EffectStats, EffectStatsDTO> {
	@Override
	public EffectStatsDTO doConvert(EffectStats source) {
		return new EffectStatsDTO(
				source.getEffectId().value(),
				source.getUptime().getSeconds()
		);
	}
}
