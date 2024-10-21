import { Component } from '@angular/core';
import { switchMap } from 'rxjs';
import { CharacterStateService } from '../../../character/services/character-state.service';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-talent-stats',
	templateUrl: './talent-stats.component.html',
	styleUrls: ['./talent-stats.component.css']
})
export class TalentStatsComponent {
	talentStats$ = this.characterStateService.characterStatChange$.pipe(
		switchMap(character => this.statsService.getTalentStats(character.characterId))
	);

	constructor(
		private characterStateService: CharacterStateService,
		private statsService: StatsService
	) {}
}
