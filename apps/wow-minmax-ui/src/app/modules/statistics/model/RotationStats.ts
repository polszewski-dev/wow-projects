import { RotationSpellStats } from "./RotationSpellStats";

export interface RotationStats {
	statList: RotationSpellStats[];
	dps: number;
	totalDamage: number;
}
