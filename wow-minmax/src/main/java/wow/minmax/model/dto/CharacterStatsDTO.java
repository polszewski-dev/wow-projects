package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import wow.commons.model.spells.SpellSchool;

import java.util.Map;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterStatsDTO {
	private String type;
	private double sp;
	private Map<SpellSchool, Double> spellDamageBySchool;
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
