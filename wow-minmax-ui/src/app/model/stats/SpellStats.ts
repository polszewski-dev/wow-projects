import { Spell } from "../spell/Spell";

export interface SpellStats {
	spell: Spell;
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
