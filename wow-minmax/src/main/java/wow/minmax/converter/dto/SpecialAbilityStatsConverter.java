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
	public SpecialAbilityStatsDTO doConvert(SpecialAbilityStats source) {
		return new SpecialAbilityStatsDTO(
				source.getAbility().getLine(),
				source.getAbility().toString(),
				source.getStatEquivalent().statString(),
				source.getSpEquivalent(),
				source.getAbility().getSource().getDescription().getName(),
				source.getAbility().getSource().getDescription().getIcon()
		);
	}
}
