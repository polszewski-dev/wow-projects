import { Ability } from "../../shared/model/spell/Ability";

export interface AbilityStats {
	ability: Ability;
	totalCastTime: number;
	numCasts: number;
	numHit: number;
	numCrit: number;
	totalDamage: number;
	dps: number;
}
