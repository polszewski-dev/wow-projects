package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.model.spell.SpellSchool;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CharacterStatsDTO {
	private String type;
	private double sp;
	private Map<SpellSchool, Integer> spellDamageBySchool;
	private double hitRating;
	private double hitPct;
	private double critRating;
	private double critPct;
	private double hasteRating;
	private double hastePct;
	private double stamina;
	private double intellect;
	private double spirit;
	private double maxHealth;
	private double maxMana;
}
