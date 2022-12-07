package wow.commons.model.config;

import lombok.Getter;
import lombok.NonNull;
import wow.commons.model.attributes.AttributeSource;
import wow.commons.model.attributes.Attributes;

/**
 * User: POlszewski
 * Date: 2022-11-24
 */
@Getter
public abstract class ConfigurationElementWithAttributes<T> extends ConfigurationElement<T> implements AttributeSource {
	@NonNull
	private final Attributes attributes;

	protected ConfigurationElementWithAttributes(T id, Description description, TimeRestriction timeRestriction, CharacterRestriction characterRestriction, Attributes attributes) {
		super(id, description, timeRestriction, characterRestriction);
		this.attributes = attributes;
	}

	@Override
	public String toString() {
		return String.format("%s [%s]", getName(), getAttributes());
	}
}
