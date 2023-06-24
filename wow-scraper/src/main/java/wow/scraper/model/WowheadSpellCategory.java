package wow.scraper.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * User: POlszewski
 * Date: 2023-05-19
 */
@AllArgsConstructor
@Getter
public enum WowheadSpellCategory {
	ENCHANTS("spells/professions/enchanting");

	private final String url;
}
