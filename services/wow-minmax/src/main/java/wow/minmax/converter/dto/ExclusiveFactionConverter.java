package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.pve.Faction;
import wow.minmax.client.dto.ExclusiveFactionDTO;

/**
 * User: POlszewski
 * Date: 2025-09-26
 */
@Component
@AllArgsConstructor
public class ExclusiveFactionConverter implements Converter<Faction, ExclusiveFactionDTO> {
	@Override
	public ExclusiveFactionDTO doConvert(Faction source) {
		return new ExclusiveFactionDTO(
				source.getId(),
				source.getName(),
				source.getExclusionGroupId()
		);
	}
}
