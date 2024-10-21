package wow.commons.repository.impl.parser.excel.mapper;

import wow.commons.model.effect.Effect;
import wow.commons.model.effect.impl.EffectImpl;
import wow.commons.model.pve.PhaseId;
import wow.commons.repository.spell.SpellRepository;
import wow.commons.util.parser.simple.ParseResult;
import wow.commons.util.parser.simple.SimpleRecordMapper;

import java.util.LinkedHashMap;

/**
 * User: POlszewski
 * Date: 2023-10-02
 */
class ItemAttributeEffectMapper extends AbstractMapper {
	public ItemAttributeEffectMapper(SpellRepository spellRepository) {
		super(spellRepository);
	}

	@Override
	public String toString(Effect effect) {
		var map = new LinkedHashMap<String, Object>();
		putAttributes(map, effect.getModifierComponent().attributes());
		return SimpleRecordMapper.toString(null, map);
	}

	@Override
	public Effect fromString(ParseResult parseResult, PhaseId phaseId) {
		var attributes = getAttributes(parseResult);
		return EffectImpl.newAttributeEffect(attributes);
	}
}
