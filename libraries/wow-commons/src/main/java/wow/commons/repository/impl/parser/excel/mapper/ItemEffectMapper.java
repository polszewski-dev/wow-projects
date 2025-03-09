package wow.commons.repository.impl.parser.excel.mapper;

import org.springframework.stereotype.Component;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.AttributesFormater;
import wow.commons.util.AttributesParser;

/**
 * User: POlszewski
 * Date: 2022-11-26
 */
@Component
public final class ItemEffectMapper {
	private final SpellRepository spellRepository;

	public ItemEffectMapper(SpellRepository spellRepository) {
		this.spellRepository = spellRepository;
	}

	public String toString(Effect effect) {
		if (effect == null) {
			return null;
		}

		if (effect.getEffectId() != 0) {
			return effect.getEffectId() + "";
		}

		if (effect.hasAnyNonModifierComponents()) {
			throw new IllegalArgumentException();
		}

		return AttributesFormater.format(effect.getModifierComponent().attributes());
	}

	public Effect fromString(String value, PhaseId phaseId) {
		if (value == null) {
			return null;
		}

		if (value.matches("^\\d+$")) {
			var effectId = Integer.parseInt(value);
			return spellRepository.getEffect(effectId, phaseId).orElseThrow();
		}

		var attributes = AttributesParser.parse(value);

		return EffectImpl.newAttributeEffect(attributes);
	}
}
