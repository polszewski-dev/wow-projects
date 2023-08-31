package wow.commons.model.spell;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.config.impl.ConfigurationElementImpl;
import wow.commons.model.talent.TalentTree;

/**
 * User: POlszewski
 * Date: 2020-09-28
 */
@Getter
public class SpellInfo extends ConfigurationElementImpl<SpellId> {
	private final TalentTree talentTree;
	private final SpellSchool spellSchool;
	private final Duration cooldown;
	private final boolean ignoresGcd;
	private final SpellTarget target;
	private final int range;
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
			boolean ignoresGcd,
			SpellTarget target,
			int range,
			DamagingSpellInfo damagingSpellInfo,
			Conversion conversion
	) {
		super(spellId, description, timeRestriction, characterRestriction);
		this.talentTree = talentTree;
		this.spellSchool = spellSchool;
		this.cooldown = cooldown;
		this.ignoresGcd = ignoresGcd;
		this.target = target;
		this.range = range;
		this.damagingSpellInfo = damagingSpellInfo;
		this.conversion = conversion;
	}
}
