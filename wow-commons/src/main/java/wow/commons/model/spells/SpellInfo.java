package wow.commons.model.spells;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.config.impl.ConfigurationElementImpl;
import wow.commons.model.talents.TalentTree;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Getter
public class SpellInfo extends ConfigurationElementImpl<SpellId> {
	private final TalentTree talentTree;
	private final SpellSchool spellSchool;
	private final Duration cooldown;
	private final boolean ignoresGCD;
	private final DamagingSpellInfo damagingSpellInfo;
	private final Conversion conversion;

	public SpellInfo(
			SpellId spellId,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			TalentTree talentTree,
			SpellSchool spellSchool,
			Duration cooldown,
			boolean ignoresGCD,
			DamagingSpellInfo damagingSpellInfo,
			Conversion conversion
	) {
		super(spellId, description, timeRestriction, characterRestriction);
		this.talentTree = talentTree;
		this.spellSchool = spellSchool;
		this.cooldown = cooldown;
		this.ignoresGCD = ignoresGCD;
		this.damagingSpellInfo = damagingSpellInfo;
		this.conversion = conversion;
	}

	public Percent getConversionPct(Conversion.From from, Conversion.To to) {
		return conversion != null && conversion.is(from, to) ? conversion.percent() : Percent.ZERO;
	}
}
