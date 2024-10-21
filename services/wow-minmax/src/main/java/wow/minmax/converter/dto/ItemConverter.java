package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.config.Described;
import wow.commons.model.item.Item;
import wow.commons.repository.item.ItemRepository;
import wow.commons.repository.pve.PhaseRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.dto.ItemDTO;
import wow.minmax.model.dto.PhaseDTO;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static wow.minmax.converter.dto.DtoConverterParams.getPhaseId;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class ItemConverter implements Converter<Item, ItemDTO>, ParametrizedBackConverter<Item, ItemDTO> {
	private final SourceConverter sourceConverter;
	private final PhaseConverter phaseConverter;
	private final ItemRepository itemRepository;
	private final PhaseRepository phaseRepository;

	@Override
	public ItemDTO doConvert(Item source) {
		return new ItemDTO(
				source.getId(),
				source.getName(),
				source.getRarity(),
				source.getItemType(),
				source.getItemSubType(),
				source.getItemLevel(),
				sourceConverter.getSources(source),
				sourceConverter.getDetailedSources(source),
				source.getSocketTypes(),
				source.getSocketBonus().getTooltip(),
				source.getIcon(),
				getTooltip(source),
				getShortTooltip(source),
				getFirstAppearedInPhase(source)
		);
	}

	@Override
	public Item doConvertBack(ItemDTO source, Map<String, Object> params) {
		return itemRepository.getItem(source.getId(), getPhaseId(params)).orElseThrow();
	}

	private String getTooltip(Item source) {
		return Stream.of(
				getEffectString(source),
				getActivatedAbilityString(source),
				getSocketString(source),
				getItemSetString(source)
		)
				.filter(x -> !x.isEmpty())
				.collect(Collectors.joining("\n"));
	}

	private String getEffectString(Item item) {
		return item.getEffects().stream()
				.map(Described::getTooltip)
				.collect(Collectors.joining("\n"));
	}

	private String getActivatedAbilityString(Item item) {
		if (item.getActivatedAbility() == null) {
			return "";
		}
		return item.getActivatedAbility().getTooltip();
	}

	private String getSocketString(Item item) {
		if (!item.hasSockets()) {
			return "";
		}

		String socketString = item.getSocketSpecification().socketTypes()
				.stream()
				.map(x -> "[" + x.name().charAt(0) + "]")
				.collect(Collectors.joining());

		return String.format("Sockets: %s %s", socketString, item.getSocketBonus().getTooltip());
	}

	private String getItemSetString(Item item) {
		if (item.getItemSet() == null) {
			return "";
		}

		return String.format("Set: %s", item.getItemSet().getName());
	}

	private String getShortTooltip(Item item) {
		return getItemSetString(item);
	}

	private PhaseDTO getFirstAppearedInPhase(Item item) {
		var phaseId = item.getFirstAppearedInPhase();
		var phase = phaseRepository.getPhase(phaseId).orElseThrow();

		return phaseConverter.convert(phase);
	}
}
