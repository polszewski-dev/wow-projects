package wow.minmax.converter.dto.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.spell.TalentRepository;
import wow.minmax.client.dto.stats.TalentStatsDTO;
import wow.minmax.converter.dto.TalentConverter;

/**
 * User: POlszewski
 * Date: 2025-09-11
 */
@Component
@AllArgsConstructor
public class TalentStatsConverter implements ParametrizedConverter<wow.estimator.client.dto.stats.TalentStatsDTO, TalentStatsDTO, PhaseId> {
	private final TalentRepository talentRepository;
	private final TalentConverter talentConverter;

	@Override
	public TalentStatsDTO doConvert(wow.estimator.client.dto.stats.TalentStatsDTO source, PhaseId phaseId) {
		var talent = talentRepository.getTalent(source.talentId(), phaseId).orElseThrow();

		return new TalentStatsDTO(
				talentConverter.convert(talent),
				source.statEquivalent(),
				source.spEquivalent()
		);
	}
}
