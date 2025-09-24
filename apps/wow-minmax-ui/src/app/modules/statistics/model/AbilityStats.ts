import { Ability } from "../../shared/model/spell/Ability";

export interface AbilityStats {
	ability: Ability;
	dps: number;
	totalDamage: number;
	castTime: number;
	manaCost: number;
	dpm: number;
	sp: number;
	totalHit: number;
	totalCrit: number;
	totalHaste: number;
	spellCoeffDirect: number;
	spellCoeffDoT: number;
	critCoeff: number;
	hitSpEqv: number;
	critSpEqv: number;
	hasteSpEqv: number;
	duration: number;
	cooldown: number;
	threatPct: number;
	pushbackPct: number;
	instantCast: boolean;
}
