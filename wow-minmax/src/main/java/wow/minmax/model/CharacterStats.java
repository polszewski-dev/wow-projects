package wow.minmax.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2023-01-01
 */
@Data
@AllArgsConstructor
public class CharacterStats {
	private double sp;
	private double spShadow;
	private double spFire;
	private double hitRating;
	private double hitPct;
	private double critRating;
	private double critPct;
	private double hasteRating;
	private double hastePct;
	private double stamina;
	private double intellect;
	private double spirit;
}
