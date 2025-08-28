package wow.minmax.converter.model;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.client.converter.Converter;
import wow.commons.model.talent.Talent;
import wow.minmax.model.TalentConfig;

/**
 * User: POlszewski
 * Date: 2023-05-10
 */
@Component
@AllArgsConstructor
public class TalentConfigConverter implements Converter<Talent, TalentConfig> {
	@Override
	public TalentConfig doConvert(Talent source) {
		return new TalentConfig(
				source.getTalentId(),
				source.getRank(),
				source.getName()
		);
	}
}
