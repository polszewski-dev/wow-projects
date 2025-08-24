package wow.estimator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.AbilityConverter;
import wow.commons.client.converter.Converter;
import wow.estimator.client.dto.stats.SpellStatsDTO;
import wow.estimator.model.SpellStats;

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
