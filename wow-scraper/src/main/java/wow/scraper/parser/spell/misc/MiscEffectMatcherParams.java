package wow.scraper.parser.spell.misc;

import wow.scraper.parser.spell.SpellMatcherParams;

/**
 * User: POlszewski
 * Date: 2023-09-06
 */
public class MiscEffectMatcherParams extends SpellMatcherParams {
	public MiscEffectMatcherParams(String line) {
		super(line);
		removeNoise();
	}
}
