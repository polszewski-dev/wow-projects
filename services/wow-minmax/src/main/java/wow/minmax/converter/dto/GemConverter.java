package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.config.Described;
import wow.commons.model.item.Gem;
import wow.commons.model.item.MetaEnabler;
import wow.commons.repository.item.GemRepository;
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
	private final GemRepository gemRepository;

	@Override
	public GemDTO doConvert(Gem source) {
		return new GemDTO(
				source.getId(),
				source.getName(),
				source.getColor(),
				source.getRarity(),
				sourceConverter.getSources(source),
				source.getName(),
				source.getIcon(),
				getTooltip(source)
		);
	}

	@Override
	public Gem doConvertBack(GemDTO source, Map<String, Object> params) {
		return gemRepository.getGem(source.getId(), getPhaseId(params)).orElseThrow();
	}

	private String getTooltip(Gem gem) {
		var attributeString = gem.getEffects().stream()
				.map(Described::getTooltip)
				.collect(Collectors.joining("\n"));
		var metaEnablerString = gem.getMetaEnablers().stream()
				.map(MetaEnabler::toString)
				.collect(Collectors.joining("\n"));

		if (metaEnablerString.isEmpty()) {
			return attributeString;
		}
		return attributeString + "\n" +
				"_".repeat(60) + "\n\n" +
				metaEnablerString;
	}
}
