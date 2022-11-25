package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.talents.Talent;
import wow.commons.repository.SpellDataRepository;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.TalentPO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class TalentPOConverter extends Converter<Talent, TalentPO> {
	private final SpellDataRepository spellDataRepository;

	@Override
	protected TalentPO doConvert(Talent talent) {
		return new TalentPO(talent.getTalentId(), talent.getRank(), talent.getTalentId().getName());
	}

	@Override
	protected Talent doConvertBack(TalentPO value) {
		return spellDataRepository.getTalent(value.getTalentId(), value.getRank()).orElseThrow();
	}
}
