import { Component, Input, OnInit } from '@angular/core';
import { Store } from '@ngrx/store';
import { Observable } from 'rxjs';
import { ItemSlot } from '../../model/equipment/ItemSlot';
import { ItemSocketStatus } from '../../model/equipment/ItemSocketStatus';
import { CharacterModuleState } from '../../state/character-module.state';
import { selectSocketStatus } from '../../state/character/character.selectors';

@Component({
	selector: 'app-socket-status',
	templateUrl: './socket-status.component.html',
	styleUrls: ['./socket-status.component.css']
})
export class SocketStatusComponent implements OnInit {
	@Input({ required: true }) itemSlot!: ItemSlot;

	socketStatus$!: Observable<ItemSocketStatus | null>;

	constructor(private store: Store<CharacterModuleState>) {}

	ngOnInit(): void {
		this.socketStatus$ = this.store.select(selectSocketStatus(this.itemSlot));
	}
}
