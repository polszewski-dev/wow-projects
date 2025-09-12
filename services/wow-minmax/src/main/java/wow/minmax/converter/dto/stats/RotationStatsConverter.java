package wow.minmax.converter.dto.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.pve.PhaseId;
import wow.minmax.client.dto.stats.RotationStatsDTO;

/**
 * User: POlszewski
 * Date: 2025-09-11
 */
@Component
@AllArgsConstructor
public class RotationStatsConverter implements ParametrizedConverter<wow.estimator.client.dto.stats.RotationStatsDTO, RotationStatsDTO, PhaseId> {
	private final RotationSpellStatsConverter rotationSpellStatsConverter;

	@Override
	public RotationStatsDTO doConvert(wow.estimator.client.dto.stats.RotationStatsDTO source, PhaseId phaseId) {
		return new RotationStatsDTO(
				rotationSpellStatsConverter.convertList(source.statList(), phaseId),
				source.dps(),
				source.totalDamage()
		);
	}
}
