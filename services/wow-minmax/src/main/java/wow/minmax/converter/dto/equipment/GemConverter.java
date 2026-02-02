package wow.minmax.converter.dto.equipment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.client.converter.ParametrizedBackConverter;
import wow.commons.model.config.Described;
import wow.commons.model.item.Gem;
import wow.commons.model.item.GemId;
import wow.commons.model.item.MetaEnabler;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.item.GemRepository;
import wow.minmax.client.dto.equipment.GemDTO;

import java.util.stream.Collectors;

/**
 * User: POlszewski
 * Date: 2021-12-13
 */
@Component
@AllArgsConstructor
public class GemConverter implements Converter<Gem, GemDTO>, ParametrizedBackConverter<Gem, GemDTO, PhaseId> {
	private final ItemSourceConverter itemSourceConverter;
	private final GemRepository gemRepository;

	@Override
	public GemDTO doConvert(Gem source) {
		return new GemDTO(
				source.getId().value(),
				source.getName(),
				source.getColor(),
				source.getRarity(),
				itemSourceConverter.getSources(source),
				source.getName(),
				source.getIcon(),
				getTooltip(source)
		);
	}

	@Override
	public Gem doConvertBack(GemDTO source, PhaseId phaseId) {
		var gemId = GemId.of(source.id());

		return gemRepository.getGem(gemId, phaseId).orElseThrow();
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
