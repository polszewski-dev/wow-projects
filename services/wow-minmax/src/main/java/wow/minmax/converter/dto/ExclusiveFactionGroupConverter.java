package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.ExclusiveFactionGroupDTO;
import wow.minmax.model.ExclusiveFactionGroup;

/**
 * User: POlszewski
 * Date: 2025-10-03
 */
@Component
@AllArgsConstructor
public class ExclusiveFactionGroupConverter implements Converter<ExclusiveFactionGroup, ExclusiveFactionGroupDTO> {
	private final ExclusiveFactionConverter exclusiveFactionConverter;

	@Override
	public ExclusiveFactionGroupDTO doConvert(ExclusiveFactionGroup source) {
		return new ExclusiveFactionGroupDTO(
				source.groupId().name(),
				exclusiveFactionConverter.convert(source.selectedFaction()),
				exclusiveFactionConverter.convertList(source.availableFactions())
		);
	}
}
