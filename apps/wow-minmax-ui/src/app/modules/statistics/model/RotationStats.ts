import { RotationAbilityStats } from "./RotationAbilityStats";

export interface RotationStats {
	statList: RotationAbilityStats[];
	dps: number;
	totalDamage: number;
}
