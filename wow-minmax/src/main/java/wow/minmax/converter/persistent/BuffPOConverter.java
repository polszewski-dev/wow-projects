package wow.minmax.converter.persistent;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import wow.commons.model.buffs.Buff;
import wow.commons.model.pve.Phase;
import wow.commons.repository.SpellDataRepository;
import wow.minmax.converter.ParametrizedConverter;
import wow.minmax.model.persistent.BuffPO;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2021-12-14
 */
@Component
@AllArgsConstructor
public class BuffPOConverter extends ParametrizedConverter<Buff, BuffPO> {
	private final SpellDataRepository spellDataRepository;

	@Override
	protected BuffPO doConvert(Buff buff, Map<String, Object> params) {
		return new BuffPO(buff.getId(), buff.getName());
	}

	@Override
	protected Buff doConvertBack(BuffPO value, Map<String, Object> params) {
		Phase phase = (Phase)params.get(PlayerProfilePOConverter.PARAM_PHASE);
		return spellDataRepository.getBuff(value.getId(), phase).orElseThrow();
	}
}
