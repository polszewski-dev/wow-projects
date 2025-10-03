import { ExclusiveFaction } from "./ExclusiveFaction";

export interface ExclusiveFactionGroup {
	groupId: string;
	selectedFaction: ExclusiveFaction;
	availableFactions: ExclusiveFaction[];
}