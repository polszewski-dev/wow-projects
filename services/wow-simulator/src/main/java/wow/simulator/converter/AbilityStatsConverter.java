package wow.simulator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.AbilityConverter;
import wow.commons.client.converter.Converter;
import wow.simulator.client.dto.AbilityStatsDTO;
import wow.simulator.model.stats.AbilityStats;

/**
 * User: POlszewski
 * Date: 2024-11-11
 */
@Component
@AllArgsConstructor
public class AbilityStatsConverter implements Converter<AbilityStats, AbilityStatsDTO> {
	private final AbilityConverter abilityConverter;

	@Override
	public AbilityStatsDTO doConvert(AbilityStats source) {
		return new AbilityStatsDTO(
				abilityConverter.convert(source.getAbility()),
				source.getTotalCastTime().getSeconds(),
				source.getNumCasts(),
				source.getNumHit(),
				source.getNumCrit(),
				source.getTotalDamage(),
				source.getDps()
		);
	}
}
