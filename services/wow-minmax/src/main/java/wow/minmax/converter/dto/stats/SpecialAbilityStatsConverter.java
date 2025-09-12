package wow.minmax.converter.dto.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.stats.SpecialAbilityStatsDTO;

/**
 * User: POlszewski
 * Date: 2025-09-11
 */
@Component
@AllArgsConstructor
public class SpecialAbilityStatsConverter implements Converter<wow.estimator.client.dto.stats.SpecialAbilityStatsDTO, SpecialAbilityStatsDTO> {
	@Override
	public SpecialAbilityStatsDTO doConvert(wow.estimator.client.dto.stats.SpecialAbilityStatsDTO source) {
		return new SpecialAbilityStatsDTO(
				source.description(),
				source.attributes(),
				source.statEquivalent(),
				source.spEquivalent(),
				source.sourceName(),
				source.sourceIcon()
		);
	}
}
