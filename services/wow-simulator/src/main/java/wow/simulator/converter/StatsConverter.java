package wow.simulator.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.ParametrizedConverter;
import wow.commons.repository.spell.SpellRepository;
import wow.simulator.client.dto.StatsDTO;
import wow.simulator.model.stats.AbilityStats;
import wow.simulator.model.stats.CooldownStats;
import wow.simulator.model.stats.EffectStats;
import wow.simulator.model.stats.Stats;
import wow.simulator.model.unit.Player;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingDouble;

/**
 * User: POlszewski
 * Date: 2024-11-11
 */
@Component
@AllArgsConstructor
public class StatsConverter implements ParametrizedConverter<Stats, StatsDTO, Player> {
	private final AbilityStatsConverter abilityStatsConverter;
	private final EffectStatsConverter effectStatsConverter;
	private final CooldownStatsConverter cooldownStatsConverter;
	private final SpellRepository spellRepository;

	@Override
	public StatsDTO doConvert(Stats source, Player player) {
		var abilityStats = source.getAbilityStats().stream()
				.sorted(
						comparingDouble(AbilityStats::getDps).reversed()
						.thenComparing(x -> x.getAbility().getName())
						.thenComparing(x -> x.getAbility().getRank())
						.thenComparing(x -> x.getAbility().getId())
				).toList();

		var effectStats = source.getEffectStats().stream()
				.sorted(
						comparing((EffectStats x) -> x.getEffect().getName())
						.thenComparing(x -> x.getEffect().getId())
				).toList();

		var cooldownStats = source.getCooldownStats().stream()
				.sorted(
						comparing((CooldownStats stats) -> getSpellName(stats, player))
						.thenComparing(x -> x.getSpellId(player))
				).toList();

		return new StatsDTO(
				abilityStatsConverter.convertList(abilityStats),
				effectStatsConverter.convertList(effectStats),
				cooldownStatsConverter.convertList(cooldownStats, player),
				source.getSimulationDuration().getSeconds(),
				source.getTotalDamage(),
				source.getDps(),
				source.getNumCasts()
		);
	}

	private String getSpellName(CooldownStats stats, Player player) {
		var spellId = stats.getSpellId(player);
		var phaseId = player.getPhaseId();
		var spell = spellRepository.getSpell(spellId, phaseId).orElseThrow();

		return spell.getName();
	}
}
