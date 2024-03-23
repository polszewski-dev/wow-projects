package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.talent.Talent;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.TalentDTO;

/**
 * User: POlszewski
 * Date: 2024-03-28
 */
@Component
@AllArgsConstructor
public class TalentConverter implements Converter<Talent, TalentDTO> {
	@Override
	public TalentDTO doConvert(Talent source) {
		return new TalentDTO(
				source.getName(),
				source.getRank(),
				source.getMaxRank(),
				source.getIcon(),
				source.getTooltip()
		);
	}
}
