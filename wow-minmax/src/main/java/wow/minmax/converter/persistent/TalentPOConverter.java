package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.talents.TalentInfo;
import wow.commons.repository.SpellDataRepository;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.TalentPO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class TalentPOConverter extends Converter<TalentInfo, TalentPO> {
	private final SpellDataRepository spellDataRepository;

	@Override
	protected TalentPO doConvert(TalentInfo talentInfo) {
		return new TalentPO(talentInfo.getTalentId(), talentInfo.getRank(), talentInfo.getTalentId().getName());
	}

	@Override
	protected TalentInfo doConvertBack(TalentPO value) {
		return spellDataRepository.getTalentInfo(value.getTalentId(), value.getRank());
	}
}
