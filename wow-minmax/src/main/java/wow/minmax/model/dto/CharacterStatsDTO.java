package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
@Data
@AllArgsConstructor
public class CharacterStatsDTO {
	private String type;
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
	private double doubleellect;
	private double spirit;
}
