import { AbilityStats } from "./AbilityStats";

export interface Stats {
	abilityStats: AbilityStats[];
	simulationDuration: number;
	totalDamage: number;
	dps: number;
}
