package wow.minmax.converter.dto.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.AbilityDTO;
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
		var ability = new AbilityDTO(
				0,
				source.sourceName(),
				null,
				source.sourceIcon(),
				source.description()
		);

		return new SpecialAbilityStatsDTO(
				ability,
				source.attributes(),
				source.statEquivalent(),
				source.spEquivalent()
		);
	}
}
