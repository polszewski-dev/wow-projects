package wow.minmax.converter.dto.simulation;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.effect.EffectId;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.spell.SpellRepository;
import wow.minmax.client.dto.simulation.SimulationEffectStatsDTO;
import wow.minmax.converter.dto.EffectConverter;
import wow.simulator.client.dto.EffectStatsDTO;

/**
 * User: POlszewski
 * Date: 2025-09-23
 */
@Component
@AllArgsConstructor
public class SimulationEffectStatsConverter implements ParametrizedConverter<EffectStatsDTO, SimulationEffectStatsDTO, PhaseId> {
	private final SpellRepository spellRepository;
	private final EffectConverter effectConverter;

	@Override
	public SimulationEffectStatsDTO doConvert(EffectStatsDTO source, PhaseId phaseId) {
		var effectId = EffectId.of(source.effectId());
		var effect = spellRepository.getEffect(effectId, phaseId).orElseThrow();

		return new SimulationEffectStatsDTO(
				effectConverter.convert(effect),
				source.uptime(),
				source.count()
		);
	}
}
