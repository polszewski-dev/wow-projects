import { NgModule } from '@angular/core';
import { CharacterModule } from '../character/character.module';
import { SimulationComponent } from './components/simulation/simulation.component';

@NgModule({
	declarations: [
		SimulationComponent
	],
	imports: [
		CharacterModule,
	],
	exports: [
		SimulationComponent
	]
})
export class SimulationModule { }
