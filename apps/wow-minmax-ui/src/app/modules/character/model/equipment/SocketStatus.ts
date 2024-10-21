import { SocketType } from './SocketType';

export interface SocketStatus {
	socketNo: number;
	socketType: SocketType;
	matching: boolean;
}
