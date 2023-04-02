package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.spells.Spell;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.SpellDTO;

/**
 * User: POlszewski
 * Date: 2022-11-25
 */
@Component
@AllArgsConstructor
public class SpellConverter implements Converter<Spell, SpellDTO> {
	@Override
	public SpellDTO doConvert(Spell source) {
		return new SpellDTO(source.getName(), source.getRank(), source.getIcon(), source.getTooltip());
	}
}
