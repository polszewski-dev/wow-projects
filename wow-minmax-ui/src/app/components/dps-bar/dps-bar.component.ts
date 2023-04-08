import { Component, Input } from '@angular/core';
import { RotationSpellStats } from 'src/app/model/character/RotationSpellStats';
import { RotationStats } from 'src/app/model/character/RotationStats';

@Component({
	selector: 'app-dps-bar',
	templateUrl: './dps-bar.component.html',
	styleUrls: ['./dps-bar.component.css']
})
export class DpsBarComponent {
	@Input() rotationStats?: RotationStats;
	@Input() dpsDiff?: number;

	getDamageShare(stats: RotationSpellStats) {
		return 100 * (stats.numCasts * stats.damage) / this.rotationStats!.totalDamage;
	}
}
