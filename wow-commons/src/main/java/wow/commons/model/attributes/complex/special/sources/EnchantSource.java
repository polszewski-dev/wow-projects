package wow.commons.model.attributes.complex.special.sources;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import wow.commons.model.attributes.complex.SpecialAbilitySource;
import wow.commons.model.config.Description;
import wow.commons.model.item.Enchant;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
public class EnchantSource implements SpecialAbilitySource, Comparable<EnchantSource> {
	private final Enchant enchant;

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
