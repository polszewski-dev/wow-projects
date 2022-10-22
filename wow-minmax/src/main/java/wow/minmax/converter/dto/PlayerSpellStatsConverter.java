package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.spells.SpellRankInfo;
import wow.commons.util.Snapshot;
import wow.commons.util.SpellStatistics;
import wow.minmax.converter.Converter;
import wow.minmax.model.PlayerSpellStats;
import wow.minmax.model.dto.SpellStatsDTO;

/**
 * User: POlszewski
 * Date: 2022-01-06
 */
@Component
@AllArgsConstructor
public class PlayerSpellStatsConverter extends Converter<PlayerSpellStats, SpellStatsDTO> {
	@Override
	protected SpellStatsDTO doConvert(PlayerSpellStats playerSpellStats) {
		SpellStatistics spellStatistics = playerSpellStats.getSpellStatistics();
		Snapshot snapshot = spellStatistics.snapshot;

		SpellInfo spellInfo = snapshot.spellInfo;
		SpellRankInfo spellRankInfo = snapshot.spellRankInfo;

		boolean dir = spellRankInfo.getMinDmg() != 0 && spellRankInfo.getMaxDmg() != 0;
		boolean dot = spellRankInfo.getDotDmg() != 0;;

		return new SpellStatsDTO(
				spellInfo.getSpellId().getName(),
				(int) spellStatistics.dps,
				(int) spellStatistics.totalDamage,
				spellStatistics.castTime.getSeconds(),
				(int) spellStatistics.manaCost,
				(int) spellStatistics.dpm,
				(int) snapshot.sp,
				snapshot.totalHit,
				dir ? snapshot.totalCrit : 0,
				snapshot.totalHaste,
				dir ? snapshot.spellCoeffDirect : 0,
				dot ? snapshot.spellCoeffDoT : 0,
				dir ? snapshot.critCoeff : 0,
				blankNearZeros(playerSpellStats.getHitSpEqv()),
				blankNearZeros(playerSpellStats.getCritSpEqv()),
				blankNearZeros(playerSpellStats.getHasteSpEqv())
		);
	}

	private static double blankNearZeros(double value) {
		return Math.abs(value) < 0.01 ? 0 : value;
	}
}
