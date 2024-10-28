import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { filter, switchMap } from 'rxjs';
import { CharacterModuleState } from 'src/app/modules/character/state/character-module.state';
import { selectDpsChanges } from 'src/app/modules/character/state/character/character.selectors';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-talent-stats',
	templateUrl: './talent-stats.component.html',
	styleUrls: ['./talent-stats.component.css']
})
export class TalentStatsComponent {
	talentStats$ = this.store.select(selectDpsChanges).pipe(
		filter(change => !!change.characterId),
		switchMap(change => this.statsService.getTalentStats(change.characterId!))
	);

	constructor(
		private store: Store<CharacterModuleState>,
		private statsService: StatsService
	) {}
}
