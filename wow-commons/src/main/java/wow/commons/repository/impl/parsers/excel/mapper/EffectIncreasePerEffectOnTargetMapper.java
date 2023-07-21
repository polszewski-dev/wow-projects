package wow.commons.repository.impl.parsers.excel.mapper;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.complex.EffectIncreasePerEffectOnTarget;
import wow.commons.model.talents.TalentTree;
import wow.commons.util.parser.simple.ParseResult;
import wow.commons.util.parser.simple.SimpleRecordMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
class EffectIncreasePerEffectOnTargetMapper extends AbstractMapper<EffectIncreasePerEffectOnTarget> {
	private static final String TYPE_EFFECT_INCREASE = "EffectIncrease";
	private static final String EI_TREE = "tree";
	private static final String EI_PCT = "%";
	private static final String EI_MAX_PCT = "max%";

	EffectIncreasePerEffectOnTargetMapper() {
		super(EffectIncreasePerEffectOnTarget.class, TYPE_EFFECT_INCREASE);
	}

	@Override
	public String toString(EffectIncreasePerEffectOnTarget effectIncrease) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(EI_TREE, effectIncrease.effectTree());
		map.put(EI_PCT, effectIncrease.increasePerEffectPct());
		map.put(EI_MAX_PCT, effectIncrease.maxIncreasePct());

		return SimpleRecordMapper.toString(TYPE_EFFECT_INCREASE, map);
	}

	@Override
	public EffectIncreasePerEffectOnTarget fromString(ParseResult parseResult) {
		var effectTree = parseResult.getEnum(EI_TREE, TalentTree::parse);
		var increasePerEffectPct = parseResult.getPercent(EI_PCT);
		var maxIncreasePct = parseResult.getPercent(EI_MAX_PCT);

		return new EffectIncreasePerEffectOnTarget(effectTree, increasePerEffectPct, maxIncreasePct, AttributeCondition.EMPTY);
	}
}
