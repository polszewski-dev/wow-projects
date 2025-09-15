package wow.minmax.converter.dto.stats;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.model.pve.PhaseId;
import wow.commons.model.spell.SpellId;
import wow.commons.repository.spell.SpellRepository;
import wow.minmax.client.dto.stats.RotationSpellStatsDTO;
import wow.minmax.converter.dto.AbilityConverter;

/**
 * User: POlszewski
 * Date: 2025-09-11
 */
@Component
@AllArgsConstructor
public class RotationSpellStatsConverter implements ParametrizedConverter<wow.estimator.client.dto.stats.RotationSpellStatsDTO, RotationSpellStatsDTO, PhaseId> {
	private final SpellRepository spellRepository;
	private final AbilityConverter abilityConverter;

	@Override
	public RotationSpellStatsDTO doConvert(wow.estimator.client.dto.stats.RotationSpellStatsDTO source, PhaseId phaseId) {
		var spellId = SpellId.of(source.spellId());
		var ability = spellRepository.getAbility(spellId, phaseId).orElseThrow();

		return new RotationSpellStatsDTO(
				abilityConverter.convert(ability),
				source.numCasts(),
				source.damage()
		);
	}
}
