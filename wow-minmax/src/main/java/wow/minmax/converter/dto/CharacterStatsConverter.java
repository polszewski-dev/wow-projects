package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.minmax.converter.Converter;
import wow.minmax.model.CharacterStats;
import wow.minmax.model.dto.CharacterStatsDTO;

/**
 * User: POlszewski
 * Date: 2023-01-01
 */
@Component
@AllArgsConstructor
public class CharacterStatsConverter extends Converter<CharacterStats, CharacterStatsDTO> {
	@Override
	protected CharacterStatsDTO doConvert(CharacterStats value) {
		return new CharacterStatsDTO(
				null,
				value.getSp(),
				value.getSpShadow(),
				value.getSpFire(),
				value.getHitRating(),
				value.getHitPct(),
				value.getCritRating(),
				value.getCritPct(),
				value.getHasteRating(),
				value.getHastePct(),
				value.getStamina(),
				value.getIntellect(),
				value.getSpirit()
		);
	}
}
