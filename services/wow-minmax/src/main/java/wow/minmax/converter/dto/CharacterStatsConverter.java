package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.snapshot.StatSummary;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.CharacterStatsDTO;

/**
 * User: POlszewski
 * Date: 2023-01-01
 */
@Component
@AllArgsConstructor
public class CharacterStatsConverter implements Converter<StatSummary, CharacterStatsDTO> {
	@Override
	public CharacterStatsDTO doConvert(StatSummary source) {
		return new CharacterStatsDTO(
				null,
				source.getSpellPower(),
				source.getSpellDamageBySchool(),
				source.getSpellHitRating(),
				source.getSpellHitPct(),
				source.getSpellCritRating(),
				source.getSpellCritPct(),
				source.getSpellHasteRating(),
				source.getSpellHastePct(),
				source.getStamina(),
				source.getIntellect(),
				source.getSpirit(),
				source.getMaxHealth(),
				source.getMaxMana()
		);
	}
}
