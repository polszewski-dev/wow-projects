import { Racial } from "./Racial";

export interface Race {
	id: string;
	name: string;
	icon: string;
	racials: Racial[];
}
