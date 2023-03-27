package wow.commons.model.config.impl;

import lombok.*;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.ConfigurationElement;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public abstract class ConfigurationElementImpl<T> implements ConfigurationElement<T> {
	@NonNull
	private final T id;

	@NonNull
	private final Description description;

	@NonNull
	private final TimeRestriction timeRestriction;

	@NonNull
	private final CharacterRestriction characterRestriction;

	@Override
	public String toString() {
		return getName();
	}
}
