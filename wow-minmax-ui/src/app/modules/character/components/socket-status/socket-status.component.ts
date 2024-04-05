import { Component, Input, OnInit } from '@angular/core';
import { Observable, map } from 'rxjs';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { ItemSocketStatus } from '../../model/equipment/ItemSocketStatus';
import { CharacterStateService } from '../../services/character-state.service';

@Component({
	selector: 'app-socket-status',
	templateUrl: './socket-status.component.html',
	styleUrls: ['./socket-status.component.css']
})
export class SocketStatusComponent implements OnInit {
	@Input() itemSlot!: ItemSlot;

	socketStatus$!: Observable<ItemSocketStatus | undefined>;

	constructor(private characterStateService: CharacterStateService) {}

	ngOnInit(): void {
		this.socketStatus$ = this.characterStateService.socketStatus$.pipe(
			map(status => status?.socketStatusesByItemSlot[this.itemSlot])
		);
	}
}
