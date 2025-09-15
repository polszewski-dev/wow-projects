package wow.minmax.converter.dto.simulation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.SpellId;
import wow.commons.repository.spell.SpellRepository;
import wow.minmax.client.dto.simulation.SimulationAbilityStatsDTO;
import wow.minmax.converter.dto.AbilityConverter;
import wow.simulator.client.dto.AbilityStatsDTO;

/**
 * User: POlszewski
 * Date: 2025-09-11
 */
@Component
@AllArgsConstructor
public class SimulationAbilityStatsConverter implements ParametrizedConverter<AbilityStatsDTO, SimulationAbilityStatsDTO, PhaseId> {
	private final SpellRepository spellRepository;
	private final AbilityConverter abilityConverter;

	@Override
	public SimulationAbilityStatsDTO doConvert(AbilityStatsDTO source, PhaseId phaseId) {
		var spellId = SpellId.of(source.abilityId());
		var ability = spellRepository.getAbility(spellId, phaseId).orElseThrow();

		return new SimulationAbilityStatsDTO(
				abilityConverter.convert(ability),
				source.totalCastTime(),
				source.numCasts(),
				source.numHit(),
				source.numCrit(),
				source.totalDamage(),
				source.dps()
		);
	}
}
