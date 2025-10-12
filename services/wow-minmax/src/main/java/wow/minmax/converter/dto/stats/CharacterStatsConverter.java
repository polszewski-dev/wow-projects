package wow.minmax.converter.dto.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.stats.CharacterStatsDTO;

/**
 * User: POlszewski
 * Date: 2025-09-11
 */
@Component
@AllArgsConstructor
public class CharacterStatsConverter implements Converter<wow.estimator.client.dto.stats.CharacterStatsDTO, CharacterStatsDTO> {
	@Override
	public CharacterStatsDTO doConvert(wow.estimator.client.dto.stats.CharacterStatsDTO source) {
		return new CharacterStatsDTO(
				source.type(),
				source.sp(),
				source.spellDamageBySchool(),
				source.hitRating(),
				source.hitPct(),
				source.critRating(),
				source.critPct(),
				source.hasteRating(),
				source.hastePct(),
				source.stamina(),
				source.intellect(),
				source.spirit(),
				source.maxHealth(),
				source.maxMana(),
				source.outOfCombatHealthRegen(),
				source.inCombatHealthRegen(),
				source.uninterruptedManaRegen(),
				source.interruptedManaRegen()
		);
	}
}
