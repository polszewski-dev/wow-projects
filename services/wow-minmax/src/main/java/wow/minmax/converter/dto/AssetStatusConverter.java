package wow.minmax.converter.dto;

import org.springframework.stereotype.Component;
import wow.character.model.asset.Asset;
import wow.minmax.client.dto.AssetDTO;

/**
 * User: POlszewski
 * Date: 2025-08-30
 */
@Component
public class AssetStatusConverter extends OptionStatusConverter<Asset, AssetDTO> {
	public AssetStatusConverter(AssetConverter converter) {
		super(converter);
	}

	@Override
	protected String getGroupKey(Asset source) {
		return source.exclusionGroup() != null
				? source.exclusionGroup().name()
				: source.name();
	}
}
