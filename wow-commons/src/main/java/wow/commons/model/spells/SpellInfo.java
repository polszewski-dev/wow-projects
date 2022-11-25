package wow.commons.model.spells;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.talents.TalentTree;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@AllArgsConstructor
@Getter
public class SpellInfo {
	private final SpellId spellId;
	private final Description description;
	private final Restriction restriction;
	private final TalentTree talentTree;
	private final SpellSchool spellSchool;
	private final Duration cooldown;
	private final boolean ignoresGCD;
	private final DamagingSpellInfo damagingSpellInfo;
	private final Conversion conversion;

	public Percent getConversionPct(Conversion.From from, Conversion.To to) {
		return conversion != null && conversion.is(from, to) ? conversion.getPercent() : Percent.ZERO;
	}
}
