import { Component, Input } from '@angular/core';
import { SimulationStats } from '../../model/SimulationStats';

@Component({
	selector: 'app-simulation-effect-stats',
	templateUrl: './simulation-effect-stats.component.html',
	styleUrl: './simulation-effect-stats.component.css'
})
export class SimulationEffectStatsComponent {
	@Input({ required: true })
	stats!: SimulationStats;
}
