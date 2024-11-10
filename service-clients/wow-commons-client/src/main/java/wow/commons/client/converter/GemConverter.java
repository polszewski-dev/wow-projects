package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.dto.GemDTO;
import wow.commons.model.config.Described;
import wow.commons.model.item.Gem;
import wow.commons.model.item.MetaEnabler;
import wow.commons.repository.item.GemRepository;

import java.util.Map;
import java.util.stream.Collectors;

import static wow.commons.client.converter.DtoConverterParams.getPhaseId;

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
		return gemRepository.getGem(source.id(), getPhaseId(params)).orElseThrow();
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
