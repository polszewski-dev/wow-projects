import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { filter, switchMap } from 'rxjs';
import { CharacterService } from '../../services/character.service';
import { CharacterModuleState } from '../../state/character-module.state';
import { selectCharacter } from '../../state/character/character.selectors';

@Component({
	selector: 'app-character-details',
	templateUrl: './character-details.component.html',
	styleUrl: './character-details.component.css'
})
export class CharacterDetailsComponent {
	character$ = this.store.select(selectCharacter);

	availableProfessions$ = this.character$.pipe(
		filter(character => !!character),
		switchMap(character => this.characterService.getAvailableProfessions(character!.characterId))
	);

	constructor(
		private store: Store<CharacterModuleState>,
		private characterService: CharacterService
	) {}
}
