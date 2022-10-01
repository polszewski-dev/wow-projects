package wow.commons.model.effects;

import wow.commons.model.spells.SpellSchool;

/**
 * User: POlszewski
 * Date: 2021-01-21
 */
public class RemoveCondition {
	private final RemoveEvent event;
	private final SpellSchool spellSchool;

	private RemoveCondition(RemoveEvent event, SpellSchool spellSchool) {
		this.event = event;
		this.spellSchool = spellSchool;
	}

	public static RemoveCondition create(RemoveEvent event, SpellSchool spellSchool) {
		if (event != null) {
			return new RemoveCondition(event, spellSchool);
		}
		if (spellSchool != null) {
			throw new IllegalArgumentException("No event");
		}
		return null;
	}

	public RemoveEvent getEvent() {
		return event;
	}

	public SpellSchool getSpellSchool() {
		return spellSchool;
	}

	public boolean matches(RemoveEvent event, SpellSchool spellSchool) {
		return this.event == event && (this.spellSchool == null || this.spellSchool == spellSchool);
	}
}
