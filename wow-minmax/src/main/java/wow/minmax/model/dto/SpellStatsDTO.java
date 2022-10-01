package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
@Data
@AllArgsConstructor
public class SpellStatsDTO {
	private String spellName;
	private int dps;
	private int totalDamage;
	private double castTime;
	private int manaCost;
	private int dpm;
	private int sp;
	private double totalHit;
	private double totalCrit;
	private double totalHaste;
	private double spellCoeffDirect;
	private double spellCoeffDoT;
	private double critCoeff;
	private double hitSpEqv;
	private double critSpEqv;
	private double hasteSpEqv;
}
