import { OptionGroup } from "./OptionGroup";
import { OptionStatus } from "./OptionStatus";

export interface Consumable {
	id: number;
	name: string;
	attributes: string;
	icon: string;
	tooltip: string;
}

export type ConsumableStatus = OptionStatus<Consumable>
export type ConsumableGroup = OptionGroup<Consumable>
