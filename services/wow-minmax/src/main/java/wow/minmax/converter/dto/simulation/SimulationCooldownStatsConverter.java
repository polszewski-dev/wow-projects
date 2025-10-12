package wow.minmax.converter.dto.simulation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.SpellId;
import wow.commons.repository.spell.SpellRepository;
import wow.minmax.client.dto.simulation.SimulationCooldownStatsDTO;
import wow.minmax.converter.dto.SpellConverter;
import wow.simulator.client.dto.CooldownStatsDTO;

/**
 * User: POlszewski
 * Date: 2025-09-23
 */
@Component
@AllArgsConstructor
public class SimulationCooldownStatsConverter implements ParametrizedConverter<CooldownStatsDTO, SimulationCooldownStatsDTO, PhaseId> {
	private final SpellRepository spellRepository;
	private final SpellConverter spellConverter;

	@Override
	public SimulationCooldownStatsDTO doConvert(CooldownStatsDTO source, PhaseId phaseId) {
		var spellId = SpellId.of(source.spellId());
		var spell = spellRepository.getSpell(spellId, phaseId).orElseThrow();

		return new SimulationCooldownStatsDTO(
				spellConverter.convert(spell),
				source.uptime(),
				source.count()
		);
	}
}
