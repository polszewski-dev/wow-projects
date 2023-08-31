package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.minmax.converter.Converter;
import wow.minmax.model.SpellStats;
import wow.minmax.model.dto.SpellStatsDTO;

/**
 * User: POlszewski
 * Date: 2022-01-06
 */
@Component
@AllArgsConstructor
public class SpellStatsConverter implements Converter<SpellStats, SpellStatsDTO> {
	private final AbilityConverter abilityConverter;

	@Override
	public SpellStatsDTO doConvert(SpellStats source) {
		return new SpellStatsDTO(
				abilityConverter.convert(source.getAbility()),
				source.getTotalDamage(),
				source.getDps(),
				source.getCastTime().getSeconds(),
				source.isInstantCast(),
				source.getManaCost(),
				source.getDpm(),
				source.getSp(),
				source.getTotalHit(),
				source.getTotalCrit(),
				source.getTotalHaste(),
				source.getSpellCoeffDirect(),
				source.getSpellCoeffDoT(),
				source.getCritCoeff(),
				source.getHitSpEqv(),
				source.getCritSpEqv(),
				source.getHasteSpEqv(),
				source.getDuration(),
				source.getCooldown(),
				source.getThreatPct(),
				source.getPushbackPct()
		);
	}
}
