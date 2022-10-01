package wow.commons.model.buffs;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.spells.SpellId;

import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2021-03-26
 */
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

	public Buff(int id, String name, int level, BuffType type, BuffExclusionGroup exclusionGroup, Attributes attributes, SpellId sourceSpell, Duration duration, Duration cooldown, String description) {
		this.id = id;
		this.name = name;
		this.level = level;
		this.type = type;
		this.exclusionGroup = exclusionGroup;
		this.attributes = attributes;
		this.sourceSpell = sourceSpell;
		this.duration = duration;
		this.cooldown = cooldown;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getLevel() {
		return level;
	}

	public BuffType getType() {
		return type;
	}

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

	public SpellId getSourceSpell() {
		return sourceSpell;
	}

	public Duration getDuration() {
		return duration;
	}

	public Duration getCooldown() {
		return cooldown;
	}

	public String getDescription() {
		return description;
	}

	public Attributes modifyEffectByPct(Percent effectIncreasePct) {
		return attributes.scale(effectIncreasePct.toMultiplier());
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Buff)) return false;
		Buff buff = (Buff) o;
		return id == buff.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return name;
	}
}
