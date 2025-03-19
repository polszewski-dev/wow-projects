package wow.evaluator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.evaluator.client.dto.stats.SpecialAbilityStatsDTO;
import wow.evaluator.model.SpecialAbilityStats;

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
				source.getAbility().getTooltip(),
				source.getAbility().toString(),
				"",
				source.getSpEquivalent(),
				source.getAbility().getSource().getName(),
				source.getAbility().getSource().getIcon()
		);
	}
}
