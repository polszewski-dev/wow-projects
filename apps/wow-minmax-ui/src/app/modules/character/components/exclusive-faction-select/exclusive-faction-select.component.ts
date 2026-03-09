import { Component, Input } from '@angular/core';
import { Store } from '@ngrx/store';
import { ExclusiveFaction } from '../../model/ExclusiveFaction';
import { ExclusiveFactionGroup } from '../../model/ExclusiveFactionGroup';
import { CharacterModuleState } from '../../state/character-module.state';
import { changeExclusiveFaction } from '../../state/character/character.actions';

@Component({
	selector: 'app-exclusive-faction-select',
	templateUrl: './exclusive-faction-select.component.html',
	styleUrl: './exclusive-faction-select.component.css'
})
export class ExclusiveFactionSelectComponent {
	@Input({ required: true }) playerId!: string;
	@Input({ required: true }) exclusiveFactionGroup!: ExclusiveFactionGroup;

	constructor(private store: Store<CharacterModuleState>) {}

	onExclusiveFactionChanged(exclusiveFaction: ExclusiveFaction) {
		const playerId = this.playerId;

		this.store.dispatch(changeExclusiveFaction({ playerId, exclusiveFaction }));
	}
}
