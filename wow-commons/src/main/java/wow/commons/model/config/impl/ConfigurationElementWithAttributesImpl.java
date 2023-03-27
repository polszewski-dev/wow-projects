package wow.commons.model.config.impl;

import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.ConfigurationElementWithAttributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public abstract class ConfigurationElementWithAttributesImpl<T> extends ConfigurationElementImpl<T> implements ConfigurationElementWithAttributes<T> {
	@NonNull
	private final Attributes attributes;

	protected ConfigurationElementWithAttributesImpl(
			T id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction,
			Attributes attributes
	) {
		super(id, description, timeRestriction, characterRestriction);
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return String.format("%s [%s]", getName(), getAttributes());
	}
}
