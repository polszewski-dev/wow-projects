package wow.minmax.converter.dto.simulation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.pve.PhaseId;
import wow.minmax.client.dto.simulation.SimulationStatsDTO;
import wow.simulator.client.dto.StatsDTO;

/**
 * User: POlszewski
 * Date: 2025-09-11
 */
@Component
@AllArgsConstructor
public class SimulationStatsConverter implements ParametrizedConverter<StatsDTO, SimulationStatsDTO, PhaseId> {
	private final SimulationAbilityStatsConverter simulationAbilityStatsConverter;
	@Override
	public SimulationStatsDTO doConvert(StatsDTO source, PhaseId phaseId) {
		return new SimulationStatsDTO(
				simulationAbilityStatsConverter.convertList(source.abilityStats(), phaseId),
				source.simulationDuration(),
				source.totalDamage(),
				source.dps(),
				source.numCasts()
		);
	}
}
