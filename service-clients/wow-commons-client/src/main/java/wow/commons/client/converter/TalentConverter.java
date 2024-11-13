package wow.commons.client.converter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.dto.TalentDTO;
import wow.commons.model.talent.Talent;

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
				source.getTalentId(),
				source.getName(),
				source.getRank(),
				source.getMaxRank(),
				source.getIcon(),
				source.getTooltip()
		);
	}
}
