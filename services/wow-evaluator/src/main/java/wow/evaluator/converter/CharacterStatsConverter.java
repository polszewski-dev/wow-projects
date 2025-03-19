package wow.evaluator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.snapshot.StatSummary;
import wow.commons.client.converter.Converter;
import wow.evaluator.client.dto.stats.CharacterStatsDTO;
import wow.evaluator.model.CharacterStats;

import java.util.ArrayList;
import java.util.List;

/**
 * User: POlszewski
 * Date: 2025-03-17
 */
@Component
@AllArgsConstructor
public class CharacterStatsConverter implements Converter<CharacterStats, List<CharacterStatsDTO>> {//
	@Override
	public List<CharacterStatsDTO> doConvert(CharacterStats source) {
		var result = new ArrayList<CharacterStatsDTO>();

		result.add(convert("Current buffs", source.current()));
		result.add(convert("Equipment", source.equipment()));
		result.add(convert("No buffs", source.nofBuffs()));
		result.add(convert("Self-buffs", source.selfBuffs()));
		result.add(convert("Party buffs", source.partyBuffs()));
		result.add(convert("Party buffs & consumes", source.partyBuffsAndConsumes()));
		result.add(convert("Raid buffs & consumes", source.raidBuffsAndConsumes()));

		if (source.worldBuffsAndConsumes() != null) {
			result.add(convert("World buffs & consumes", source.worldBuffsAndConsumes()));
		}

		return result;
	}

	private CharacterStatsDTO convert(String type, StatSummary source) {
		return new CharacterStatsDTO(
				type,
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
