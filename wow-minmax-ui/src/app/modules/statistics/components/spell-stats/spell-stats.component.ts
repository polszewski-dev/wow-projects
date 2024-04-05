import { Component } from '@angular/core';
import { switchMap } from 'rxjs';
import { CharacterStateService } from '../../../character/services/character-state.service';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-spell-stats',
	templateUrl: './spell-stats.component.html',
	styleUrls: ['./spell-stats.component.css']
})
export class SpellStatsComponent {
	spellStatsList$ = this.characterStateService.characterStatChange$.pipe(
		switchMap(character => this.statsService.getSpellStats(character.characterId))
	);

	constructor(
		private characterStateService: CharacterStateService,
		private statsService: StatsService
	) {}
}
