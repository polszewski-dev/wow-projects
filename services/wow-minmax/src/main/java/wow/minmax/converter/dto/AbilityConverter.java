package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.spell.Ability;
import wow.minmax.client.dto.AbilityDTO;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@Component
@AllArgsConstructor
public class AbilityConverter implements Converter<Ability, AbilityDTO> {
	@Override
	public AbilityDTO doConvert(Ability source) {
		return new AbilityDTO(
				source.getId().value(),
				source.getName(),
				source.getRank(),
				source.getIcon(),
				source.getTooltip()
		);
	}
}
