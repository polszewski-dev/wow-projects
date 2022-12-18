package wow.commons.repository.impl.parsers.excel.mapper;

import wow.commons.model.attributes.complex.SpecialAbility;
import wow.commons.model.attributes.complex.special.MiscAbility;
import wow.commons.util.parser.simple.ParseResult;
import wow.commons.util.parser.simple.SimpleRecordMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
class MiscAbilityMapper extends AbstractMapper<MiscAbility> {
	private static final String TYPE_MISC = "Misc";
	private static final String M_LINE = "line";

	protected MiscAbilityMapper() {
		super(MiscAbility.class, TYPE_MISC);
	}

	@Override
	public String toString(MiscAbility talentProcAbility) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put(M_LINE, talentProcAbility.getLine());

		return SimpleRecordMapper.toString(TYPE_MISC, map);
	}

	@Override
	public MiscAbility fromString(ParseResult parseResult) {
		var line = parseResult.getString(M_LINE);

		return SpecialAbility.misc(line);
	}
}
