package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.minmax.converter.Converter;
import wow.minmax.model.SpecialAbilityStats;
import wow.minmax.model.dto.SpecialAbilityStatsDTO;

/**
 * User: POlszewski
 * Date: 2023-01-01
 */
@Component
@AllArgsConstructor
public class SpecialAbilityStatsConverter extends Converter<SpecialAbilityStats, SpecialAbilityStatsDTO> {
	@Override
	protected SpecialAbilityStatsDTO doConvert(SpecialAbilityStats value) {
		return new SpecialAbilityStatsDTO(
			value.getDescription(),
			value.getAbility(),
			value.getStatEquivalent().statString(),
			value.getSpEquivalent()
		);
	}
}