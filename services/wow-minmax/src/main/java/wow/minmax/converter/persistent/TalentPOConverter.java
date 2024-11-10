package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.talent.Talent;
import wow.commons.client.converter.Converter;
import wow.minmax.model.persistent.TalentPO;

/**
 * User: POlszewski
 * Date: 2023-05-10
 */
@Component
@AllArgsConstructor
public class TalentPOConverter implements Converter<Talent, TalentPO> {
	@Override
	public TalentPO doConvert(Talent source) {
		return new TalentPO(
				source.getTalentId(),
				source.getRank(),
				source.getName()
		);
	}
}
