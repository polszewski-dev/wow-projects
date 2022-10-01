package wow.minmax.converter.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.talents.TalentInfo;
import wow.minmax.converter.Converter;
import wow.minmax.model.dto.TalentDTO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class TalentConverter extends Converter<TalentInfo, TalentDTO> {
	@Override
	protected TalentDTO doConvert(TalentInfo talentInfo) {
		return new TalentDTO(talentInfo.getTalentId(), talentInfo.getRank(), talentInfo.getTalentId().getName());
	}
}
