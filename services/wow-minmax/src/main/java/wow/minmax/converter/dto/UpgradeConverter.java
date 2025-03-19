package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.EquippableItemConverter;
import wow.commons.client.util.AttributeFormatter;
import wow.commons.model.config.Described;
import wow.minmax.client.dto.UpgradeDTO;
import wow.minmax.model.AttributesDiff;
import wow.minmax.model.SpecialAbility;
import wow.minmax.model.Upgrade;

import java.util.List;

/**
 * User: POlszewski
 * Date: 2022-01-11
 */
@Component
@AllArgsConstructor
public class UpgradeConverter implements Converter<Upgrade, UpgradeDTO> {
	private final EquippableItemConverter equippableItemConverter;

	@Override
	public UpgradeDTO doConvert(Upgrade source) {
		AttributesDiff statDifference = source.getStatDifference();

		return new UpgradeDTO(
				source.changePct(),
				equippableItemConverter.convertList(source.getItemDifference()),
				getStatDiff(statDifference),
				getAbilities(statDifference.addedAbilities()),
				getAbilities(statDifference.removedAbilities())
		);
	}

	private List<String> getStatDiff(AttributesDiff statDifference) {
		return statDifference
				.attributes()
				.list()
				.stream()
				.map(AttributeFormatter::format)
				.toList();
	}

	private List<String> getAbilities(List<SpecialAbility> abilities) {
		return abilities.stream()
				.map(Described::getTooltip)
				.distinct()
				.sorted()
				.toList();
	}
}
