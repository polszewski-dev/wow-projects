import { Component, Input } from '@angular/core';
import { SimulationStats } from '../../model/SimulationStats';

@Component({
	selector: 'app-simulation-cooldown-stats',
	templateUrl: './simulation-cooldown-stats.component.html',
	styleUrl: './simulation-cooldown-stats.component.css'
})
export class SimulationCooldownStatsComponent {
	@Input({ required: true })
	stats!: SimulationStats;
}
