import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { CharacterModuleState } from 'src/app/modules/character/state/character-module.state';
import { selectPlayerId } from '../../modules/character/state/character/character.selectors';

@Component({
	selector: 'app-main',
	templateUrl: './main.component.html',
	styleUrls: ['./main.component.css']
})
export class MainComponent {
	selectedPlayerId$ = this.store.select(selectPlayerId);

	constructor(private store: Store<CharacterModuleState>) {}
}
