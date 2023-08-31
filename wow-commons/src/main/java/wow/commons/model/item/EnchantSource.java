package wow.commons.model.item;

import wow.commons.model.config.Description;
import wow.commons.model.effect.EffectSource;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record EnchantSource(Enchant enchant) implements EffectSource, Comparable<EnchantSource> {
	@Override
	public Description getDescription() {
		return enchant.getDescription();
	}

	@Override
	public int getPriority() {
		return 3;
	}

	@Override
	public int compareTo(EnchantSource o) {
		return Integer.compare(this.enchant.getId(), o.enchant.getId());
	}
}
