import { NgModule } from '@angular/core';
import { CharacterModule } from '../character/character.module';
import { SimulationComponent } from './components/simulation/simulation.component';
import { SimulationAbilityStatsComponent } from './components/simulation-ability-stats/simulation-ability-stats.component';
import { SimulationCooldownStatsComponent } from './components/simulation-cooldown-stats/simulation-cooldown-stats.component';
import { SimulationEffectStatsComponent } from './components/simulation-effect-stats/simulation-effect-stats.component';

@NgModule({
	declarations: [
		SimulationComponent,
		SimulationAbilityStatsComponent,
		SimulationEffectStatsComponent,
		SimulationCooldownStatsComponent,
	],
	imports: [
		CharacterModule,
	],
	exports: [
		SimulationComponent,
		SimulationAbilityStatsComponent,
		SimulationEffectStatsComponent,
		SimulationCooldownStatsComponent,
	]
})
export class SimulationModule { }
