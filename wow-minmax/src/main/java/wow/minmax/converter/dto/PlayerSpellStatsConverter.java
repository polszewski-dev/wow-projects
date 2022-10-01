package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.attributes.AttributeId;
import wow.commons.model.spells.SpellInfo;
import wow.commons.model.spells.SpellRankInfo;
import wow.commons.util.Snapshot;
import wow.commons.util.SpellStatistics;
import wow.minmax.converter.Converter;
import wow.minmax.model.PlayerProfile;
import wow.minmax.model.PlayerSpellStats;
import wow.minmax.model.Spell;
import wow.minmax.model.dto.SpellStatsDTO;
import wow.minmax.service.CalculationService;
import wow.minmax.service.SpellService;

/**
 * User: POlszewski
 * Date: 2022-01-06
 */
@Component
@AllArgsConstructor
public class PlayerSpellStatsConverter extends Converter<PlayerSpellStats, SpellStatsDTO> {
	private final CalculationService calculationService;
	private final SpellService spellService;

	@Override
	protected SpellStatsDTO doConvert(PlayerSpellStats playerSpellStats) {
		PlayerProfile playerProfile = playerSpellStats.getPlayerProfile();
		SpellStatistics spellStatistics = playerSpellStats.getSpellStatistics();
		Snapshot snapshot = spellStatistics.snapshot;

		SpellInfo spellInfo = snapshot.spellInfo;
		SpellRankInfo spellRankInfo = snapshot.spellRankInfo;
		Spell spell = spellService.getSpell(spellInfo.getSpellId());

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
				blankNearZeros(calculationService.getSpEquivalent(AttributeId.SpellHitRating, 10, playerProfile, spell)),
				blankNearZeros(calculationService.getSpEquivalent(AttributeId.SpellCritRating, 10, playerProfile, spell)),
				blankNearZeros(calculationService.getSpEquivalent(AttributeId.SpellHasteRating, 10, playerProfile, spell))
		);
	}

	private static double blankNearZeros(double value) {
		return Math.abs(value) < 0.01 ? 0 : value;
	}
}
