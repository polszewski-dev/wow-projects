package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.buffs.Buff;
import wow.commons.repository.SpellDataRepository;
import wow.minmax.converter.Converter;
import wow.minmax.model.persistent.BuffPO;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class BuffPOConverter extends Converter<Buff, BuffPO> {
	private final SpellDataRepository spellDataRepository;

	@Override
	protected BuffPO doConvert(Buff buff) {
		return new BuffPO(buff.getId(), buff.getName());
	}

	@Override
	protected Buff doConvertBack(BuffPO value) {
		return spellDataRepository.getBuff(value.getId()).orElseThrow();
	}
}
