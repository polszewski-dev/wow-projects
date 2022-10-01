package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Item;
import wow.commons.model.sources.Source;
import wow.commons.model.spells.SpellSchool;
import wow.commons.repository.ItemDataRepository;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.ItemDTO;
import wow.minmax.service.UpgradeService;

import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class ItemConverter extends Converter<Item, ItemDTO> {
	private final UpgradeService upgradeService;
	private final ItemDataRepository itemDataRepository;

	@Override
	protected ItemDTO doConvert(Item item) {
		return new ItemDTO(
				item.getId(),
				item.getName(),
				item.getRarity(),
				item.getItemType(),
				upgradeService.getItemScore(item, SpellSchool.Shadow),
				getSources(item),
				item.getAttributes().statString(),
				item.getSocketCount(),
				item.getSocketType(1),
				item.getSocketType(2),
				item.getSocketType(3),
				item.getSocketSpecification().getSocketBonus().statString()
		);
	}

	private String getSources(Item item) {
		return item.getSources().stream().map(this::getSourceString).distinct().collect(Collectors.joining(", "));
	}

	private String getSourceString(Source source) {
		if (source.getInstance() != null) {
			return source.getInstance()
						 .getShortName();
		}
		if (source.isBadgeVendor()) {
			return "BoJ";
		}
		if (source.isPurchasedFromVendor()) {
			return "Vendor";
		}
		if (source.isPvp()) {
			return "PvP";
		}
		if (source.isCrafted()) {
			return source.toString();
		}
		if (source.isReputationReward()) {
			return source.getFaction().getName();
		}
		if (source.isTradedFromToken()) {
			return getSources(itemDataRepository.getItem(source.getTradedFromToken()));
		}
		return source.toString();
	}
}
