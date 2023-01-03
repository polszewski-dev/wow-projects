package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.talents.Talent;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.TalentDTO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class TalentConverter implements Converter<Talent, TalentDTO> {
	@Override
	public TalentDTO doConvert(Talent talent) {
		return new TalentDTO(talent.getTalentId(), talent.getRank(), talent.getTalentId().getName());
	}
}
