package wow.scraper.parser.spell.proc;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.parser.spell.SpellPattern;
import wow.scraper.parser.spell.params.EffectPatternParams;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-09-06
 */
public class ProcPattern extends SpellPattern<EffectPatternParams> {
	public ProcPattern(String pattern, EffectPatternParams params, Set<GameVersionId> requiredVersion) {
		super(pattern, params, requiredVersion);
	}
}
