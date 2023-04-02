package wow.minmax.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpellStatsDTO {
	private SpellDTO spell;
	private double dps;
	private double totalDamage;
	private double castTime;
	private double manaCost;
	private double dpm;
	private double sp;
	private double totalHit;
	private double totalCrit;
	private double totalHaste;
	private double spellCoeffDirect;
	private double spellCoeffDoT;
	private double critCoeff;
	private double hitSpEqv;
	private double critSpEqv;
	private double hasteSpEqv;
	private double duration;
	private double cooldown;
	private double threatPct;
	private double pushbackPct;
	private boolean instantCast;
}
