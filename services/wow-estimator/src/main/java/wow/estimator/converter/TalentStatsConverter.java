package wow.estimator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.TalentConverter;
import wow.estimator.client.dto.stats.TalentStatsDTO;
import wow.estimator.model.TalentStats;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
@Component
@AllArgsConstructor
public class TalentStatsConverter implements Converter<TalentStats, TalentStatsDTO> {
	private final TalentConverter talentConverter;

	@Override
	public TalentStatsDTO doConvert(TalentStats source) {
		return new TalentStatsDTO(
				talentConverter.convert(source.talent()),
				source.statEquivalent(),
				source.spEquivalent()
		);
	}
}
