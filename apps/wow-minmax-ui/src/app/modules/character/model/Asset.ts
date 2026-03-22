import { OptionGroup } from "./OptionGroup";
import { OptionStatus } from "./OptionStatus";

export interface Asset {
	id: number;
	name: string;
	icon: string;
	tooltip: string;
}

export type AssetStatus = OptionStatus<Asset>
export type AssetGroup = OptionGroup<Asset>
