package wow.commons.model.buffs.impl;

import lombok.Getter;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.attributes.complex.special.sources.BuffSource;
import wow.commons.model.buffs.Buff;
import wow.commons.model.buffs.BuffExclusionGroup;
import wow.commons.model.buffs.BuffType;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.config.impl.ConfigurationElementWithAttributesImpl;
import wow.commons.model.spells.SpellId;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public class BuffImpl extends ConfigurationElementWithAttributesImpl<Integer> implements Buff {
	private final BuffType type;
	private final BuffExclusionGroup exclusionGroup;
	private final SpellId sourceSpell;

	public BuffImpl(
			Integer id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			BuffType type,
			BuffExclusionGroup exclusionGroup,
			SpellId sourceSpell
	) {
		super(id, description, timeRestriction, characterRestriction);
		this.type = type;
		this.exclusionGroup = exclusionGroup;
		this.sourceSpell = sourceSpell;
	}

	@Override
	protected SpecialAbilitySource getSpecialAbilitySource() {
		return new BuffSource(this);
	}
}
