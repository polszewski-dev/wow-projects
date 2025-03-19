package wow.evaluator.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import wow.character.model.character.Character;
import wow.commons.model.Duration;
import wow.commons.model.spell.Ability;

/**
 * User: POlszewski
 * Date: 2022-01-06
 */
@AllArgsConstructor
@Getter
public class SpellStats {
	private Character character;
	private Ability ability;
	private double totalDamage;
	private double dps;
	private Duration castTime;
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
