import { Component } from '@angular/core';
import { switchMap } from 'rxjs';
import { CharacterStateService } from '../../../character/services/character-state.service';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-special-abilities',
	templateUrl: './special-abilities.component.html',
	styleUrls: ['./special-abilities.component.css']
})
export class SpecialAbilitiesComponent {
	specialAbilityStats$ = this.characterStateService.characterStatChange$.pipe(
		switchMap(character => this.statsService.getSpecialAbilities(character.characterId))
	);

	constructor(
		private characterStateService: CharacterStateService,
		private statsService: StatsService
	) {}
}
