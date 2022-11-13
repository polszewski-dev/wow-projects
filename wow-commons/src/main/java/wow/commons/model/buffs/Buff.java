package wow.commons.model.buffs;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.spells.SpellId;

/**
 * User: POlszewski
 * Date: 2021-03-26
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode(of = "id")
public class Buff implements AttributeSource {
	private final int id;
	private final String name;
	private final int level;
	private final BuffType type;
	private final BuffExclusionGroup exclusionGroup;
	private final Attributes attributes;
	private final SpellId sourceSpell;
	private final Duration duration;
	private final Duration cooldown;
	private final String description;

	public BuffExclusionGroup getExclusionGroup() {
		return exclusionGroup != null ? exclusionGroup : type.getDefaultExclusionGroup();
	}

	@Override
	public Attributes getAttributes() {
		if (duration != null && cooldown != null) {
			SpecialAbility onUseAbility = SpecialAbility.onUse(attributes, duration, cooldown, description);
			return Attributes.of(onUseAbility);
		}
		return attributes;
	}

	public Attributes modifyEffectByPct(Percent effectIncreasePct) {
		return attributes.scale(effectIncreasePct.toMultiplier());
	}

	@Override
	public String toString() {
		return name;
	}
}
