package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.spell.Ability;
import wow.commons.client.dto.AbilityDTO;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@Component
@AllArgsConstructor
public class AbilityConverter implements Converter<Ability, AbilityDTO> {
	@Override
	public AbilityDTO doConvert(Ability source) {
		return new AbilityDTO(source.getName(), source.getRank(), source.getIcon(), source.getTooltip());
	}
}
