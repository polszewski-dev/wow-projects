package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.buffs.Buff;
import wow.commons.repository.SpellRepository;
import wow.minmax.converter.Converter;
import wow.minmax.converter.ParametrizedBackConverter;
import wow.minmax.model.persistent.BuffPO;

import java.util.Map;

import static wow.minmax.converter.persistent.PoConverterParams.getPhase;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class BuffPOConverter implements Converter<Buff, BuffPO>, ParametrizedBackConverter<Buff, BuffPO> {
	private final SpellRepository spellRepository;

	@Override
	public BuffPO doConvert(Buff buff) {
		return new BuffPO(buff.getId(), buff.getName());
	}

	@Override
	public Buff doConvertBack(BuffPO value, Map<String, Object> params) {
		return spellRepository.getBuff(value.getId(), getPhase(params)).orElseThrow();
	}
}
