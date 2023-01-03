package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.item.Gem;
import wow.commons.repository.ItemRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.dto.GemDTO;

import java.util.Map;

import static wow.minmax.converter.dto.DtoConverterParams.getPhase;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class GemConverter implements Converter<Gem, GemDTO>, ParametrizedBackConverter<Gem, GemDTO> {
	private final SourceConverter sourceConverter;
	private final ItemRepository itemRepository;

	@Override
	public GemDTO doConvert(Gem gem) {
		return new GemDTO(
				gem.getId(),
				gem.getName(),
				gem.getColor(),
				gem.getRarity(),
				sourceConverter.getSources(gem),
				gem.getAttributes().statString(),
				gem.getShorterName(),
				gem.getIcon(),
				null
		);
	}

	@Override
	public Gem doConvertBack(GemDTO value, Map<String, Object> params) {
		return itemRepository.getGem(value.getId(), getPhase(params)).orElseThrow();
	}
}
