package wow.minmax.converter.dto.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.pve.PhaseId;
import wow.minmax.client.dto.stats.SpellStatsDTO;

/**
 * User: POlszewski
 * Date: 2025-09-10
 */
@Component
@AllArgsConstructor
public class SpellStatsConverter implements ParametrizedConverter<wow.estimator.client.dto.stats.SpellStatsDTO, SpellStatsDTO, PhaseId> {
	@Override
	public SpellStatsDTO doConvert(wow.estimator.client.dto.stats.SpellStatsDTO source, PhaseId phaseId) {
		return new SpellStatsDTO(
				source.spell(),
				source.totalDamage(),
				source.dps(),
				source.castTime(),
				source.instantCast(),
				source.manaCost(),
				source.dpm(),
				source.sp(),
				source.totalHit(),
				source.totalCrit(),
				source.totalHaste(),
				source.spellCoeffDirect(),
				source.spellCoeffDoT(),
				source.critCoeff(),
				source.hitSpEqv(),
				source.critSpEqv(),
				source.hasteSpEqv(),
				source.duration(),
				source.cooldown(),
				source.threatPct(),
				source.pushbackPct()
		);
	}
}
