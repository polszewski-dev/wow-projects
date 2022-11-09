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
		Snapshot snapshot = spellStatistics.getSnapshot();

		SpellInfo spellInfo = snapshot.getSpellInfo();
		SpellRankInfo spellRankInfo = snapshot.getSpellRankInfo();

		boolean dir = spellRankInfo.getMinDmg() != 0 && spellRankInfo.getMaxDmg() != 0;
		boolean dot = spellRankInfo.getDotDmg() != 0;

		return new SpellStatsDTO(
				spellInfo.getSpellId().getName(),
				(int) spellStatistics.getDps(),
				(int) spellStatistics.getTotalDamage(),
				spellStatistics.getCastTime().getSeconds(),
				(int) spellStatistics.getManaCost(),
				(int) spellStatistics.getDpm(),
				(int) snapshot.getSp(),
				snapshot.getTotalHit(),
				dir ? snapshot.getTotalCrit() : 0,
				snapshot.getTotalHaste(),
				dir ? snapshot.getSpellCoeffDirect() : 0,
				dot ? snapshot.getSpellCoeffDoT() : 0,
				dir ? snapshot.getCritCoeff() : 0,
				blankNearZeros(playerSpellStats.getHitSpEqv()),
				blankNearZeros(playerSpellStats.getCritSpEqv()),
				blankNearZeros(playerSpellStats.getHasteSpEqv())
		);
	}

	private static double blankNearZeros(double value) {
		return Math.abs(value) < 0.01 ? 0 : value;
	}
}
