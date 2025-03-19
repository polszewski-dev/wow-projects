package wow.evaluator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.AbilityConverter;
import wow.commons.client.converter.Converter;
import wow.evaluator.client.dto.stats.RotationSpellStatsDTO;
import wow.evaluator.model.RotationSpellStats;

/**
 * User: POlszewski
 * Date: 2023-04-07
 */
@Component
@AllArgsConstructor
public class RotationSpellStatsConverter implements Converter<RotationSpellStats, RotationSpellStatsDTO> {
	private final AbilityConverter abilityConverter;

	@Override
	public RotationSpellStatsDTO doConvert(RotationSpellStats source) {
		return new RotationSpellStatsDTO(
				abilityConverter.convert(source.getSpell()),
				source.getNumCasts(),
				source.getDamage()
		);
	}
}
