package wow.simulator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.simulator.client.dto.StatsDTO;
import wow.simulator.model.stats.AbilityStats;
import wow.simulator.model.stats.Stats;

import static java.util.Comparator.comparingDouble;

/**
 * User: POlszewski
 * Date: 2024-11-11
 */
@Component
@AllArgsConstructor
public class StatsConverter implements Converter<Stats, StatsDTO> {
	private final AbilityStatsConverter abilityStatsConverter;

	@Override
	public StatsDTO doConvert(Stats source) {
		var abilityStats = source.getAbilityStats().stream()
				.sorted(
						comparingDouble(AbilityStats::getDps).reversed()
						.thenComparing(x -> x.getAbility().getAbilityId())
				)
				.toList();

		return new StatsDTO(
				abilityStatsConverter.convertList(abilityStats),
				source.getSimulationDuration().getSeconds(),
				source.getTotalDamage(),
				source.getDps()
		);
	}
}
