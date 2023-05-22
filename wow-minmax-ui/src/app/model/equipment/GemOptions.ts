import { Gem } from "./Gem";
import { SocketType } from "./SocketType";

export interface GemOptions {
	socketType: SocketType;
	gems: Gem[];
}
