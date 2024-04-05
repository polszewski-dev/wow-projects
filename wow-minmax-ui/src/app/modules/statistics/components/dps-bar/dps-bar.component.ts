import { Component } from '@angular/core';
import { filter, switchMap, tap } from 'rxjs';
import { CharacterStateService } from '../../../character/services/character-state.service';
import { RotationSpellStats } from '../../model/RotationSpellStats';
import { RotationStats } from '../../model/RotationStats';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-dps-bar',
	templateUrl: './dps-bar.component.html',
	styleUrls: ['./dps-bar.component.css']
})
export class DpsBarComponent {
	rotationStats$ = this.characterStateService.characterStatChange$.pipe(
		switchMap(character => this.statsService.getRotationStats(character.characterId).pipe(
			tap(rotationStats => {
				this.previousRotationStats = this.currentRotationStats;
				this.currentRotationStats = rotationStats;
			})
		))
	);

	private currentRotationStats?: RotationStats;
	private previousRotationStats?: RotationStats;

	constructor(
		private characterStateService: CharacterStateService,
		private statsService: StatsService
	) {}

	get dpsDiff() {
		if (this.previousRotationStats === undefined) {
			return 0;
		}
		return this.currentRotationStats!.dps - this.previousRotationStats.dps;
	}

	getDamageShare(stats: RotationSpellStats) {
		return 100 * (stats.numCasts * stats.damage) / this.currentRotationStats!.totalDamage;
	}
}
