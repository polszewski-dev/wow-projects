package wow.scraper.parser.spell.params;

import wow.commons.model.spell.AbilityId;
import wow.scraper.parser.scraper.ScraperPatternParams;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * User: POlszewski
 * Date: 2023-09-27
 */
public record EffectPatternParams(
		AbilityId augmentedAbility,
		PeriodicComponentParams periodicComponent,
		String tickInterval,
		ModifierComponentParams modifierComponent,
		AbsorptionComponentParams absorptionComponent,
		List<StatConversionParams> statConversions,
		Map<Integer, EventParams> events,
		String maxStacks,
		String duration
) implements ScraperPatternParams {
	public EffectPatternParams {
		Objects.requireNonNull(statConversions);
		Objects.requireNonNull(events);
	}

	public void setEvent(int eventIdx, EventParams event) {
		Objects.requireNonNull(event);
		if (getEvent(eventIdx) != null) {
			throw new IllegalArgumentException("Event[%s] already defined".formatted(eventIdx));
		}
		this.events.put(eventIdx, event);
	}

	public void setTriggeredSpell(int eventIdx, SpellPatternParams triggeredSpell) {
		Objects.requireNonNull(triggeredSpell);
		EventParams newEvent = getEvent(eventIdx).setTriggeredSpell(triggeredSpell);
		this.events.put(eventIdx, newEvent);
	}

	public EventParams getEvent(int eventIdx) {
		return events.get(eventIdx);
	}

	public Collection<EventParams> getEvents() {
		return events.values();
	}

	public SpellPatternParams getTriggeredSpell(int eventIdx) {
		return getEvent(eventIdx).triggeredSpell();
	}

	public boolean hasAnyEffectComponents() {
		return periodicComponent != null ||
				modifierComponent != null ||
				absorptionComponent != null ||
				tickInterval != null ||
				!statConversions.isEmpty() ||
				!events.isEmpty();
	}
}
