package wow.scraper.parser.spell.misc;

import wow.commons.model.effect.Effect;
import wow.scraper.parser.spell.SpellMatcher;
import wow.scraper.parser.spell.params.EffectPatternParams;

/**
 * User: POlszewski
 * Date: 2023-09-27
 */
public class MiscEffectMatcher extends SpellMatcher<MiscEffectPattern, EffectPatternParams, MiscEffectMatcherParams> {
	public MiscEffectMatcher(MiscEffectPattern pattern) {
		super(pattern);
	}

	public Effect getEffect() {
		return getEffect(getPatternParams());
	}
}
