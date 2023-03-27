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
public class SpecialAbilityStatsConverter implements Converter<SpecialAbilityStats, SpecialAbilityStatsDTO> {
	@Override
	public SpecialAbilityStatsDTO doConvert(SpecialAbilityStats value) {
		return new SpecialAbilityStatsDTO(
			value.getAbility().getLine(),
			value.getAbility().toString(),
			value.getStatEquivalent().statString(),
			value.getSpEquivalent(),
			value.getAbility().getSource().getDescription().getName(),
			value.getAbility().getSource().getDescription().getIcon()
		);
	}
}
