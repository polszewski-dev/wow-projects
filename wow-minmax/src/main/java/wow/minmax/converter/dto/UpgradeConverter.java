package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.attribute.Attribute;
import wow.commons.model.attribute.complex.ComplexAttribute;
import wow.commons.model.attribute.complex.ComplexAttributeId;
import wow.minmax.converter.Converter;
import wow.minmax.model.Comparison;
import wow.minmax.model.dto.UpgradeDTO;
import wow.minmax.util.AttributesDiff;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-01-11
 */
@Component
@AllArgsConstructor
public class UpgradeConverter implements Converter<Comparison, UpgradeDTO> {
	private final EquippableItemConverter equippableItemConverter;

	@Override
	public UpgradeDTO doConvert(Comparison source) {
		AttributesDiff statDifference = source.getStatDifference();

		return new UpgradeDTO(
				source.changePct().value(),
				equippableItemConverter.convertList(source.getItemDifference()),
				getStatDiff(statDifference),
				getAbilities(statDifference.getAddedAbilities()),
				getAbilities(statDifference.getRemovedAbilities())
		);
	}

	private List<String> getStatDiff(AttributesDiff statDifference) {
		return statDifference
				.getAttributes()
				.getPrimitiveAttributes()
				.stream()
				.map(Object::toString)
				.toList();
	}

	private List<String> getAbilities(Map<ComplexAttributeId, List<ComplexAttribute>> map) {
		return map.values().stream()
				.flatMap(Collection::stream)
				.map(Attribute::toString)
				.distinct()
				.sorted()
				.toList();
	}
}
