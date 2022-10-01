package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.attributes.ComplexAttribute;
import wow.commons.util.AttributesDiff;
import wow.minmax.converter.Converter;
import wow.minmax.model.Comparison;
import wow.minmax.model.dto.UpgradeDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * User: POlszewski
 * Date: 2022-01-11
 */
@Component
@AllArgsConstructor
public class UpgradeConverter extends Converter<Comparison, UpgradeDTO> {
	private final EquippableItemConverter equippableItemConverter;

	@Override
	protected UpgradeDTO doConvert(Comparison value) {
		AttributesDiff statDifference = value.getStatDifference();

		return new UpgradeDTO(
				Math.round(100.0 * value.changePct.getValue()) / 100.0,
				equippableItemConverter.convertList(value.getItemDifference()),
				statDifference
						.getAttributes()
						.getPrimitiveAttributeList()
						.stream()
						.map(Object::toString)
						.collect(Collectors.toList()),
				getAbilityDiff(statDifference)
		);
	}

	private List<String> getAbilityDiff(AttributesDiff statDifference) {
		List<String> abilityDifference = new ArrayList<>();

		Stream.concat(
				statDifference.getAddedAbilities()
						.keySet()
						.stream(),
				statDifference.getRemovedAbilities()
						.keySet()
						.stream()
		)
				.distinct()
				.sorted()
				.forEach(attributeId -> {
					List<ComplexAttribute> added = statDifference.getAddedAbilities()
							.getOrDefault(attributeId, List.of());
					List<ComplexAttribute> removed = statDifference.getRemovedAbilities()
							.getOrDefault(attributeId, List.of());

					if (!added.isEmpty()) {
						abilityDifference.add(String.format("++%s=%s", attributeId, added));
					}
					if (!removed.isEmpty()) {
						abilityDifference.add(String.format("--%s=%s", attributeId, removed));
					}
				});

		return abilityDifference;
	}
}
