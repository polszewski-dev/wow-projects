package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.spell.Spell;
import wow.minmax.client.dto.SpellDTO;

/**
 * User: POlszewski
 * Date: 2025-09-23
 */
@Component
@AllArgsConstructor
public class SpellConverter implements Converter<Spell, SpellDTO> {
	@Override
	public SpellDTO doConvert(Spell source) {
		return new SpellDTO(
				source.getId().value(),
				source.getName(),
				source.getIcon(),
				source.getTooltip()
		);
	}
}
