import { PhaseId } from "./PhaseId";

export interface Phase {
	id: PhaseId;
	name: string;
	maxLevel: number;
}
