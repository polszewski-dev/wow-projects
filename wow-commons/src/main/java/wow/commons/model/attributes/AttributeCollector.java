package wow.commons.model.attributes;

import wow.commons.model.attributes.complex.ComplexAttribute;

import java.util.Collection;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public interface AttributeCollector<T extends AttributeCollector<T>> {
	T addAttributes(AttributeSource attributeSource);

	default T addAttributes(Collection<? extends AttributeSource> attributeSources) {
		for (AttributeSource attributeSource : attributeSources) {
			addAttributes(attributeSource);
		}
		return (T)this;
	}

	T addAttribute(ComplexAttribute complexAttribute);
}
