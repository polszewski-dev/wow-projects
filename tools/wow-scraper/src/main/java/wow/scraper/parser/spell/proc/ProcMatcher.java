package wow.scraper.parser.spell.proc;

import wow.commons.model.Duration;
import wow.commons.model.Percent;
import wow.commons.model.effect.Effect;
import wow.commons.model.effect.component.Event;
import wow.commons.model.effect.component.EventAction;
import wow.commons.model.spell.impl.SpellImpl;
import wow.scraper.parser.spell.SpellMatcher;
import wow.scraper.parser.spell.params.EffectPatternParams;
import wow.scraper.parser.spell.params.EventParams;

import java.util.List;
import java.util.Optional;

import static wow.scraper.util.CommonAssertions.assertBothAreEqual;

/**
 * User: POlszewski
 * Date: 2023-09-06
 */
public class ProcMatcher extends SpellMatcher<ProcPattern, EffectPatternParams, ProcMatcherParams> {
	public ProcMatcher(ProcPattern pattern) {
		super(pattern);
	}

	public Effect getEffect() {
		return getEffect(getPatternParams(), this::addMissingTriggerData);
	}

	private Event addMissingTriggerData(EventParams eventParams, Event event) {
		var triggeredSpell = event.triggeredSpell();

		if (triggeredSpell != null) {
			var cooldown = getCooldown(eventParams).orElse(Duration.ZERO);
			((SpellImpl) triggeredSpell).setCooldown(cooldown);
		}

		return new Event(
				event.types(),
				event.condition(),
				getProcChance(eventParams).orElseThrow(),
				List.of(EventAction.TRIGGER_SPELL),
				triggeredSpell
		);
	}

	private Optional<Percent> getProcChance(EventParams eventParams) {
		var providedProcChance = getOptionalPercent(eventParams.chancePct());
		var parsedProcChance = matcherParams.getParsedProcChance();

		return getPresent(providedProcChance, parsedProcChance);
	}

	private Optional<Duration> getCooldown(EventParams eventParams) {
		var providedCooldown = getOptionalDuration(eventParams.cooldown());
		var parsedCooldown = getParsedCooldown();

		return getPresent(providedCooldown, parsedCooldown);
	}

	private Optional<Duration> getParsedCooldown() {
		var parsedProcCooldown = matcherParams.getParsedProcCooldown();
		var parsedCooldown = matcherParams.getParsedCooldown();

		return getPresent(parsedProcCooldown, parsedCooldown);
	}

	private <T> Optional<T> getPresent(Optional<T> first, Optional<T> second) {
		if (first.isPresent() && second.isPresent()) {
			assertBothAreEqual("", first, second);
			return first;
		}
		return first.isPresent() ? first : second;
	}
}
