package wow.scraper.parser.spell.activated;

import wow.commons.model.Duration;
import wow.commons.model.config.Description;
import wow.commons.model.spell.ActivatedAbility;
import wow.commons.model.spell.impl.ActivatedAbilityImpl;
import wow.scraper.parser.spell.SpellMatcher;
import wow.scraper.parser.spell.params.SpellPatternParams;

import java.util.Optional;

/**
 * User: POlszewski
 * Date: 2023-09-03
 */
public class ActivatedAbilityMatcher extends SpellMatcher<ActivatedAbilityPattern, SpellPatternParams, ActivatedAbilityMatcherParams> {
	public ActivatedAbilityMatcher(ActivatedAbilityPattern pattern) {
		super(pattern);
	}

	public ActivatedAbility getActivatedAbility() {
		var spell = getSpell(getPatternParams(), () -> new ActivatedAbilityImpl(null));
		spell.setCooldown(getCooldown().orElse(Duration.ZERO));
		spell.setDescription(new Description("", null, getOriginalLine()));
		return spell;
	}

	private Optional<Duration> getCooldown() {
		if (matcherParams.getParsedProcCooldown().isPresent() || matcherParams.getParsedProcChance().isPresent()) {
			throw new IllegalArgumentException();
		}
		return matcherParams.getParsedCooldown();
	}
}
