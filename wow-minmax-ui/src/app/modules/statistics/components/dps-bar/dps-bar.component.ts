import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { RotationSpellStats } from '../../model/RotationSpellStats';
import { RotationStats } from '../../model/RotationStats';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-dps-bar',
	templateUrl: './dps-bar.component.html',
	styleUrls: ['./dps-bar.component.css']
})
export class DpsBarComponent implements OnChanges {
	@Input() selectedCharacterId!: string;

	rotationStats?: RotationStats;
	previousRotationStats?: RotationStats;
	dpsDiff = 0;

	constructor(private statsService: StatsService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.updateDps();
	}

	updateDps() {
		this.statsService.getRotationStats(this.selectedCharacterId!).subscribe(rotationStats => {
			this.previousRotationStats = this.rotationStats;
			this.rotationStats = rotationStats;
			if (this.previousRotationStats !== undefined) {
				this.dpsDiff = this.rotationStats.dps - this.previousRotationStats.dps;
			} else {
				this.dpsDiff = 0;
			}
		})
	}

	getDamageShare(stats: RotationSpellStats) {
		return 100 * (stats.numCasts * stats.damage) / this.rotationStats!.totalDamage;
	}
}
