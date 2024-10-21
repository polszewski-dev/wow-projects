package wow.scraper.parser.spell.misc;

import wow.commons.model.pve.GameVersionId;
import wow.scraper.parser.spell.SpellPattern;
import wow.scraper.parser.spell.params.EffectPatternParams;

import java.util.Set;

/**
 * User: POlszewski
 * Date: 2023-09-27
 */
public class MiscEffectPattern extends SpellPattern<EffectPatternParams> {
	public MiscEffectPattern(String pattern, EffectPatternParams params, Set<GameVersionId> requiredVersion) {
		super(pattern, params, requiredVersion);
	}
}
