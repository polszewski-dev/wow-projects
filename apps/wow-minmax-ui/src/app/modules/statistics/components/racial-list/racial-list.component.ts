import { Component } from '@angular/core';
import { CharacterStateService } from '../../../character/services/character-state.service';

@Component({
	selector: 'app-racial-list',
	templateUrl: './racial-list.component.html',
	styleUrls: ['./racial-list.component.css']
})
export class RacialListComponent {
	character$ = this.characterStateService.character$;

	constructor(private characterStateService: CharacterStateService) {}
}
