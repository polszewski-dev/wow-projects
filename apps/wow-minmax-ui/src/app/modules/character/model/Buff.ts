import { OptionGroup } from "./OptionGroup";
import { OptionStatus } from "./OptionStatus";

export interface Buff {
	id: number;
	name: string;
	rank: number;
	attributes: string;
	icon: string;
	tooltip: string;
}

export type BuffStatus = OptionStatus<Buff>
export type BuffGroup = OptionGroup<Buff>
