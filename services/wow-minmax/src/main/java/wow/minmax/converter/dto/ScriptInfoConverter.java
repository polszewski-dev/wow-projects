package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.minmax.client.dto.ScriptInfoDTO;
import wow.minmax.model.config.ScriptInfo;

/**
 * User: POlszewski
 * Date: 2025-09-27
 */
@Component
@AllArgsConstructor
public class ScriptInfoConverter implements Converter<ScriptInfo, ScriptInfoDTO> {
	@Override
	public ScriptInfoDTO doConvert(ScriptInfo source) {
		return new ScriptInfoDTO(
				source.path(),
				source.name()
		);
	}
}
