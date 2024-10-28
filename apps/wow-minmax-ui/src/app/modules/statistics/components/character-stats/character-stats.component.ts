import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { combineLatest, filter, map, switchMap } from 'rxjs';
import { CharacterModuleState } from 'src/app/modules/character/state/character-module.state';
import { selectCharacter, selectDpsChanges } from 'src/app/modules/character/state/character/character.selectors';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-character-stats',
	templateUrl: './character-stats.component.html',
	styleUrls: ['./character-stats.component.css']
})
export class CharacterStatsComponent {
	data$ = combineLatest({
		change: this.store.select(selectDpsChanges),
		character: this.store.select(selectCharacter),
	}).pipe(
		filter(({ change }) => !!change.characterId),
		filter(({ character }) => !!character),
		switchMap(({ character }) => this.statsService.getCharacterStats(character!.characterId).pipe(
			map(characterStats => ({ characterStats, character: character! }))
		))
	);

	constructor(
		private store: Store<CharacterModuleState>,
		private statsService: StatsService
	) {}
}
