package wow.commons.model.buffs;

import lombok.Getter;
import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.ConfigurationElementWithAttributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.Restriction;
import wow.commons.model.spells.SpellId;

/**
 * User: POlszewski
 * Date: 2021-03-26
 */
@Getter
public class Buff extends ConfigurationElementWithAttributes<Integer> {
	private final BuffType type;
	private final BuffExclusionGroup exclusionGroup;
	private final SpellId sourceSpell;
	private final Duration duration;
	private final Duration cooldown;

	public Buff(
			Integer id,
			Description description,
			Restriction restriction,
			BuffType type,
			BuffExclusionGroup exclusionGroup,
			Attributes attributes,
			SpellId sourceSpell,
			Duration duration,
			Duration cooldown) {
		super(id, description, restriction, attributes);
		this.type = type;
		this.exclusionGroup = exclusionGroup;
		this.sourceSpell = sourceSpell;
		this.duration = duration;
		this.cooldown = cooldown;
	}

	public BuffExclusionGroup getExclusionGroup() {
		return exclusionGroup != null ? exclusionGroup : type.getDefaultExclusionGroup();
	}

	public Attributes modifyEffectByPct(Percent effectIncreasePct) {
		return getAttributes().scale(effectIncreasePct.toMultiplier());
	}
}
