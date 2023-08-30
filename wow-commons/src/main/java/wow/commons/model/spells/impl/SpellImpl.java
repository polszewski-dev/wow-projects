package wow.commons.model.spells.impl;

import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.model.config.impl.ConfigurationElementImpl;
import wow.commons.model.spells.*;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public class SpellImpl extends ConfigurationElementImpl<SpellIdAndRank> implements Spell {
	@NonNull
	private final SpellInfo spellInfo;
	@NonNull
	private final CastInfo castInfo;
	private final AppliedEffect appliedEffect;
	private final DirectDamageInfo directDamageInfo;
	private final DotDamageInfo dotDamageInfo;

	public SpellImpl(
			SpellIdAndRank id,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			Description description,
			SpellInfo spellInfo,
			CastInfo castInfo,
			AppliedEffect appliedEffect,
			DirectDamageInfo directDamageInfo,
			DotDamageInfo dotDamageInfo
	) {
		super(id, description, timeRestriction, characterRestriction);
		this.spellInfo = spellInfo;
		this.castInfo = castInfo;
		this.appliedEffect = appliedEffect;
		this.directDamageInfo = directDamageInfo;
		this.dotDamageInfo = dotDamageInfo;
	}

	@Override
	public String toString() {
		return getId().toString();
	}
}
