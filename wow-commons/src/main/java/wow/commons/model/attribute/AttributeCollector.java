package wow.commons.model.attribute;

import wow.commons.model.attribute.complex.ComplexAttribute;
import wow.commons.model.spell.SpellId;

import java.util.Collection;

/**
 * User: POlszewski
 * Date: 2022-01-05
 */
public interface AttributeCollector {
	AttributeCollector addAttributes(AttributeSource attributeSource);

	AttributeCollector addAttributes(AttributeSource attributeSource, SpellId sourceSpell);

	default AttributeCollector addAttributes(Collection<? extends AttributeSource> attributeSources) {
		for (AttributeSource attributeSource : attributeSources) {
			addAttributes(attributeSource);
		}
		return this;
	}

	AttributeCollector addAttribute(ComplexAttribute complexAttribute);
}
