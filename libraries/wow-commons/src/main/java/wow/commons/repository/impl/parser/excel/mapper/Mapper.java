package wow.commons.repository.impl.parser.excel.mapper;

import wow.commons.model.effect.Effect;
import wow.commons.model.pve.PhaseId;
import wow.commons.util.parser.simple.ParseResult;

/**
 * User: POlszewski
 * Date: 2022-12-18
 */
interface Mapper {
	String toString(Effect effect);

	Effect fromString(ParseResult parseResult, PhaseId phaseId);
}
