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
	private final SimulationEffectStatsConverter simulationEffectStatsConverter;
	private final SimulationCooldownStatsConverter simulationCooldownStatsConverter;

	@Override
	public SimulationStatsDTO doConvert(StatsDTO source, PhaseId phaseId) {
		var abilityStats = simulationAbilityStatsConverter.convertList(source.abilityStats(), phaseId);
		var effectStats = simulationEffectStatsConverter.convertList(source.effectStats(), phaseId);
		var cooldownStats = simulationCooldownStatsConverter.convertList(source.cooldownStats(), phaseId);

		return new SimulationStatsDTO(
				abilityStats,
				effectStats,
				cooldownStats,
				source.simulationDuration(),
				source.totalDamage(),
				source.dps(),
				source.numCasts()
		);
	}
}
