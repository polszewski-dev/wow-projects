import { AbilityStats } from "./AbilityStats";

export interface SimulationStats {
	abilityStats: AbilityStats[];
	simulationDuration: number;
	totalDamage: number;
	dps: number;
	numCasts: number;
}
