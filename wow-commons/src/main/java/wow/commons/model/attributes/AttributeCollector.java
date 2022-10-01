package wow.commons.model.attributes;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public interface AttributeCollector<T extends AttributeCollector<T>> {
	T addAttributes(AttributeSource attributeSource);

	default T addAttributes(List<? extends AttributeSource> attributeSources) {
		for (AttributeSource attributeSource : attributeSources) {
			addAttributes(attributeSource);
		}
		return (T)this;
	}

	T addAttribute(ComplexAttribute complexAttribute);
}
