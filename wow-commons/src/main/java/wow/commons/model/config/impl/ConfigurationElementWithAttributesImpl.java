package wow.commons.model.config.impl;

import lombok.Getter;
import wow.commons.model.attributes.Attributes;
import wow.commons.model.attributes.complex.special.SpecialAbilitySource;
import wow.commons.model.config.CharacterRestriction;
import wow.commons.model.config.ConfigurationElementWithAttributes;
import wow.commons.model.config.Description;
import wow.commons.model.config.TimeRestriction;
import wow.commons.util.AttributesBuilder;

/**
 * User: POlszewski
 * Date: 2023-03-27
 */
@Getter
public abstract class ConfigurationElementWithAttributesImpl<T> extends ConfigurationElementImpl<T> implements ConfigurationElementWithAttributes<T> {
	private Attributes attributes;

	protected ConfigurationElementWithAttributesImpl(
			T id,
			Description description,
			TimeRestriction timeRestriction,
			CharacterRestriction characterRestriction
	) {
		super(id, description, timeRestriction, characterRestriction);
	}

	public void setAttributes(Attributes attributes) {
		this.attributes = AttributesBuilder.attachSource(attributes, getSpecialAbilitySource());
	}

	protected abstract SpecialAbilitySource getSpecialAbilitySource();

	@Override
	public String toString() {
		return String.format("%s [%s]", getName(), getAttributes());
	}
}
