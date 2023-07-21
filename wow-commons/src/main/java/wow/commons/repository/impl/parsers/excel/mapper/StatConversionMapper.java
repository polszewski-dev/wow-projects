package wow.commons.repository.impl.parsers.excel.mapper;

import wow.commons.model.attributes.AttributeCondition;
import wow.commons.model.attributes.complex.StatConversion;
import wow.commons.model.attributes.primitive.PrimitiveAttributeId;
import wow.commons.util.parser.simple.ParseResult;
import wow.commons.util.parser.simple.SimpleRecordMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
class StatConversionMapper extends AbstractMapper<StatConversion> {
	private static final String TYPE_STAT_CONVERSION = "StatConversion";
	private static final String SC_FROM = "from";
	private static final String SC_TO = "to";
	private static final String SC_RATIO_PCT = "ratio%";

	protected StatConversionMapper() {
		super(StatConversion.class, TYPE_STAT_CONVERSION);
	}

	@Override
	public String toString(StatConversion statConversion) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(SC_FROM, statConversion.fromStat());
		map.put(SC_TO, statConversion.toStat());
		map.put(SC_RATIO_PCT, statConversion.ratioPct());

		return SimpleRecordMapper.toString(TYPE_STAT_CONVERSION, map);
	}

	@Override
	public StatConversion fromString(ParseResult parseResult) {
		var fromStat = parseResult.getEnum(SC_FROM, PrimitiveAttributeId::parse);
		var toStat = parseResult.getEnum(SC_TO, PrimitiveAttributeId::parse);
		var ratioPct = parseResult.getPercent(SC_RATIO_PCT);

		return new StatConversion(fromStat, toStat, ratioPct, AttributeCondition.EMPTY);
	}
}
