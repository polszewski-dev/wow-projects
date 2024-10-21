import { EquippableItem } from '../equipment/EquippableItem';

export interface Upgrade {
	changePct: number;
	itemDifference: EquippableItem[];
	statDifference: string[];
	addedAbilities: string[];
	removedAbilities: string[];
}
