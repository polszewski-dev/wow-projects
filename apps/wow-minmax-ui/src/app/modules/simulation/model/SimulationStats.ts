import { AbilityStats } from "./AbilityStats";
import { EffectStats } from './EffectStats';
import { CooldownStats } from './CooldownStats';

export interface SimulationStats {
	abilityStats: AbilityStats[];
	effectStats: EffectStats[];
	cooldownStats: CooldownStats[];
	simulationDuration: number;
	totalDamage: number;
	dps: number;
	numCasts: number;
}
