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
public class SpellConverter extends Converter<Spell, SpellDTO> {
	@Override
	protected SpellDTO doConvert(Spell spell) {
		return new SpellDTO(spell.getName(), spell.getIcon(), spell.getTooltip());
	}
}
