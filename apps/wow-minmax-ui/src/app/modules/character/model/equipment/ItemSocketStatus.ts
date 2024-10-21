import { SocketBonusStatus } from './SocketBonusStatus';
import { SocketStatus } from './SocketStatus';

export interface ItemSocketStatus {
	socketStatuses: SocketStatus[];
	socketBonusStatus: SocketBonusStatus;
}
