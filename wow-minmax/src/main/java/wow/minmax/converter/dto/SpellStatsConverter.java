package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.snapshot.Snapshot;
import wow.character.model.snapshot.SpellStatistics;
import wow.commons.model.spells.Spell;
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
	private final SpellConverter spellConverter;

	@Override
	public SpellStatsDTO doConvert(SpellStats spellStats) {
		SpellStatistics spellStatistics = spellStats.getSpellStatistics();
		Snapshot snapshot = spellStatistics.getSnapshot();

		Spell spell = snapshot.getSpell();

		boolean dir = spell.hasDirectComponent();
		boolean dot = spell.hasDotComponent();

		return new SpellStatsDTO(
				spellConverter.convert(spell),
				spellStatistics.getDps(),
				spellStatistics.getTotalDamage(),
				spellStatistics.getCastTime().getSeconds(),
				spellStatistics.getManaCost(),
				spellStatistics.getDpm(),
				snapshot.getSp(),
				snapshot.getTotalHit(),
				dir ? snapshot.getTotalCrit() : 0,
				snapshot.getTotalHaste(),
				dir ? snapshot.getSpellCoeffDirect() : 0,
				dot ? snapshot.getSpellCoeffDoT() : 0,
				dir ? snapshot.getCritCoeff() : 0,
				spellStats.getHitSpEqv(),
				spellStats.getCritSpEqv(),
				spellStats.getHasteSpEqv()
		);
	}
}
