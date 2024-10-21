import { EquippableItem } from "./EquippableItem";
import { Gem } from "./Gem";
import { SocketType } from "./SocketType";

export interface GemOptions {
	socketType: SocketType;
	gems: Gem[];
}

export function getMatchingGemOptions(gemOptions: GemOptions[], equippedItem: EquippableItem, socketIdx: number) {
	const socketType = equippedItem.item.socketTypes[socketIdx];
	return gemOptions.find(x => x.socketType === socketType)?.gems || []
}
