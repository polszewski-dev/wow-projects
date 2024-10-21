import { Component } from '@angular/core';
import { switchMap } from 'rxjs';
import { CharacterStateService } from '../../../character/services/character-state.service';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-character-stats',
	templateUrl: './character-stats.component.html',
	styleUrls: ['./character-stats.component.css']
})
export class CharacterStatsComponent {
	characterStats$ = this.characterStateService.characterStatChange$.pipe(
		switchMap(character => this.statsService.getCharacterStats(character.characterId))
	);

	constructor(
		private characterStateService: CharacterStateService,
		private statsService: StatsService
	) {}

	get character() {
		return this.characterStateService.characterSnapshot!;
	}
}
