import { Spell } from "../../shared/model/spell/Spell";

export interface AbilityStats {
	ability: Spell;
	totalCastTime: number;
	numCasts: number;
	numHit: number;
	numCrit: number;
	totalDamage: number;
	dps: number;
}
