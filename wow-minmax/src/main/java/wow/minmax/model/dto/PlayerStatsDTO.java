package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
@Data
@AllArgsConstructor
public class PlayerStatsDTO {
	private String type;
	private int sp;
	private int spShadow;
	private int spFire;
	private int hitRating;
	private double hitPct;
	private int critRating;
	private double critPct;
	private int hasteRating;
	private double hastePct;
	private int stamina;
	private int intellect;
	private int spirit;
}
