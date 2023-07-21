package wow.commons.model.item;

import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.config.Description;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
public record EnchantSource(Enchant enchant) implements SpecialAbilitySource, Comparable<EnchantSource> {
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
		return this.enchant.getId().compareTo(o.enchant.getId());
	}
}
