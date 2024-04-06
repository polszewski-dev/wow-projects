import { Component, Input } from '@angular/core';
import { ItemSocketStatus } from '../../model/equipment/ItemSocketStatus';

@Component({
	selector: 'app-socket-status',
	templateUrl: './socket-status.component.html',
	styleUrls: ['./socket-status.component.css']
})
export class SocketStatusComponent {
	@Input() socketStatus?: ItemSocketStatus;
}
