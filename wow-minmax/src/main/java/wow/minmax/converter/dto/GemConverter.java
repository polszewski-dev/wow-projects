package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.config.Described;
import wow.commons.model.item.Gem;
import wow.commons.repository.ItemRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.dto.GemDTO;

import java.util.Map;
import java.util.stream.Collectors;

import static wow.minmax.converter.dto.DtoConverterParams.getPhaseId;

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
	public GemDTO doConvert(Gem source) {
		return new GemDTO(
				source.getId(),
				source.getName(),
				source.getColor(),
				source.getRarity(),
				sourceConverter.getSources(source),
				source.getEffects().stream().map(Described::getTooltip).collect(Collectors.joining("\n")),
				source.getName(),
				source.getIcon(),
				null
		);
	}

	@Override
	public Gem doConvertBack(GemDTO source, Map<String, Object> params) {
		return itemRepository.getGem(source.getId(), getPhaseId(params)).orElseThrow();
	}
}
