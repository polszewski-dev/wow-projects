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
public class CharacterStatsConverter implements Converter<CharacterStats, CharacterStatsDTO> {
	@Override
	public CharacterStatsDTO doConvert(CharacterStats source) {
		return new CharacterStatsDTO(
				null,
				source.getSp(),
				source.getSpellDamageBySchool(),
				source.getHitRating(),
				source.getHitPct(),
				source.getCritRating(),
				source.getCritPct(),
				source.getHasteRating(),
				source.getHastePct(),
				source.getStamina(),
				source.getIntellect(),
				source.getSpirit()
		);
	}
}
