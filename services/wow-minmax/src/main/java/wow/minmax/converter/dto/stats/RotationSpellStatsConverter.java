package wow.minmax.converter.dto.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.pve.PhaseId;
import wow.minmax.client.dto.stats.RotationSpellStatsDTO;

/**
 * User: POlszewski
 * Date: 2025-09-11
 */
@Component
@AllArgsConstructor
public class RotationSpellStatsConverter implements ParametrizedConverter<wow.estimator.client.dto.stats.RotationSpellStatsDTO, RotationSpellStatsDTO, PhaseId> {
	@Override
	public RotationSpellStatsDTO doConvert(wow.estimator.client.dto.stats.RotationSpellStatsDTO source, PhaseId phaseId) {
		return new RotationSpellStatsDTO(
				source.spell(),
				source.numCasts(),
				source.damage()
		);
	}
}
