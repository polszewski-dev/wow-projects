package wow.minmax.client.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wow.commons.client.dto.AbilityDTO;

/**
 * User: POlszewski
 * Date: 2022-01-02
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SpellStatsDTO {
	private AbilityDTO spell;
	private double totalDamage;
	private double dps;
	private double castTime;
	private boolean instantCast;
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
}
