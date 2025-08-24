package wow.estimator.client.converter.upgrade;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.character.model.equipment.GemFilter;
import wow.commons.client.converter.BackConverter;
import wow.commons.client.converter.Converter;
import wow.estimator.client.dto.upgrade.GemFilterDTO;

/**
 * User: POlszewski
 * Date: 2023-04-05
 */
@Component
@AllArgsConstructor
public class GemFilterConverter implements Converter<GemFilter, GemFilterDTO>, BackConverter<GemFilter, GemFilterDTO> {
	@Override
	public GemFilterDTO doConvert(GemFilter source) {
		return new GemFilterDTO(
				source.isUnique()
		);
	}

	@Override
	public GemFilter doConvertBack(GemFilterDTO source) {
		return new GemFilter(
				source.unique()
		);
	}
}
