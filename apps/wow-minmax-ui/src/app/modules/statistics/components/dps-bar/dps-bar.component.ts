import { Component } from '@angular/core';
import { Store } from '@ngrx/store';
import { filter, switchMap, tap } from 'rxjs';
import { CharacterModuleState } from 'src/app/modules/character/state/character-module.state';
import { selectDpsChanges } from 'src/app/modules/character/state/character/character.selectors';
import { RotationSpellStats } from '../../model/RotationSpellStats';
import { RotationStats } from '../../model/RotationStats';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-dps-bar',
	templateUrl: './dps-bar.component.html',
	styleUrls: ['./dps-bar.component.css']
})
export class DpsBarComponent {
	rotationStats$ = this.store.select(selectDpsChanges).pipe(
		filter(change => !!change.characterId),
		filter(change => change.dpsChangeIdx !== this.dpsChangeIdx),
		tap(change => this.dpsChangeIdx = change.dpsChangeIdx),
		switchMap(change => this.statsService.getRotationStats(change.characterId!).pipe(
			tap(rotationStats => {
				this.previousRotationStats = this.currentRotationStats;
				this.currentRotationStats = rotationStats;
			})
		))
	);

	private dpsChangeIdx = -1;
	private currentRotationStats?: RotationStats;
	private previousRotationStats?: RotationStats;

	constructor(
		private store: Store<CharacterModuleState>,
		private statsService: StatsService
	) {}

	get dpsDiff() {
		if (!this.previousRotationStats) {
			return 0;
		}
		return this.currentRotationStats!.dps - this.previousRotationStats.dps;
	}

	getDamageShare(stats: RotationSpellStats) {
		return 100 * (stats.numCasts * stats.damage) / this.currentRotationStats!.totalDamage;
	}
}
