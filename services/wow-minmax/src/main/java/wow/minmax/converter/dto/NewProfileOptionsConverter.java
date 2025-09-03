package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.CharacterClassConverter;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.NewProfileOptionsDTO;
import wow.minmax.model.NewProfileOptions;

/**
 * User: POlszewski
 * Date: 2025-09-03
 */
@Component
@AllArgsConstructor
public class NewProfileOptionsConverter implements Converter<NewProfileOptions, NewProfileOptionsDTO> {
	private final CharacterClassConverter characterClassConverter;

	@Override
	public NewProfileOptionsDTO doConvert(NewProfileOptions source) {
		return new NewProfileOptionsDTO(
				characterClassConverter.convertList(source.classOptions())
		);
	}
}
