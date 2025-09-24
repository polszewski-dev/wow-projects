package wow.minmax.converter.dto.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.SpellId;
import wow.commons.repository.spell.SpellRepository;
import wow.minmax.client.dto.stats.AbilityStatsDTO;
import wow.minmax.converter.dto.AbilityConverter;

/**
 * User: POlszewski
 * Date: 2025-09-10
 */
@Component
@AllArgsConstructor
public class AbilityStatsConverter implements ParametrizedConverter<wow.estimator.client.dto.stats.AbilityStatsDTO, AbilityStatsDTO, PhaseId> {
	private final SpellRepository spellRepository;
	private final AbilityConverter abilityConverter;

	@Override
	public AbilityStatsDTO doConvert(wow.estimator.client.dto.stats.AbilityStatsDTO source, PhaseId phaseId) {
		var spellId = SpellId.of(source.spellId());
		var ability = spellRepository.getAbility(spellId, phaseId).orElseThrow();

		return new AbilityStatsDTO(
				abilityConverter.convert(ability),
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
