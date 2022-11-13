package wow.commons.model.effects;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.commons.model.spells.SpellSchool;

/**
 * User: POlszewski
 * Date: 2021-01-21
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class RemoveCondition {
	private final RemoveEvent event;
	private final SpellSchool spellSchool;

	public static RemoveCondition create(RemoveEvent event, SpellSchool spellSchool) {
		if (event != null) {
			return new RemoveCondition(event, spellSchool);
		}
		if (spellSchool != null) {
			throw new IllegalArgumentException("No event");
		}
		return null;
	}

	public boolean matches(RemoveEvent event, SpellSchool spellSchool) {
		return this.event == event && (this.spellSchool == null || this.spellSchool == spellSchool);
	}
}
