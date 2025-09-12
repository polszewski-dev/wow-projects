package wow.minmax.converter.dto.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.pve.PhaseId;
import wow.minmax.client.dto.stats.TalentStatsDTO;

/**
 * User: POlszewski
 * Date: 2025-09-11
 */
@Component
@AllArgsConstructor
public class TalentStatsConverter implements ParametrizedConverter<wow.estimator.client.dto.stats.TalentStatsDTO, TalentStatsDTO, PhaseId> {
	@Override
	public TalentStatsDTO doConvert(wow.estimator.client.dto.stats.TalentStatsDTO source, PhaseId phaseId) {
		return new TalentStatsDTO(
				source.talent(),
				source.statEquivalent(),
				source.spEquivalent()
		);
	}
}
