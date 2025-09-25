import { Component, Input } from '@angular/core';
import { SimulationStats } from '../../model/SimulationStats';

@Component({
	selector: 'app-simulation-ability-stats',
	templateUrl: './simulation-ability-stats.component.html',
	styleUrl: './simulation-ability-stats.component.css'
})
export class SimulationAbilityStatsComponent {
	@Input({ required: true })
	stats!: SimulationStats;
}
