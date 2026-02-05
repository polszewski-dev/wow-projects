package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.asset.Asset;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.AssetDTO;

/**
 * User: POlszewski
 * Date: 2026-03-10
 */
@Component
@AllArgsConstructor
public class AssetConverter implements Converter<Asset, AssetDTO> {
	@Override
	public AssetDTO doConvert(Asset source) {
		return new AssetDTO(
				source.id().value(),
				source.getName(),
				source.getIcon(),
				source.getTooltip()
		);
	}
}
