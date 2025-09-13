import { Component } from '@angular/core';
import { SimulationService } from '../../services/simulation.service';
import { Store } from '@ngrx/store';
import { CharacterModuleState } from 'src/app/modules/character/state/character-module.state';
import { selectDpsChanges } from 'src/app/modules/character/state/character/character.selectors';
import { filter, switchMap } from 'rxjs/operators';

@Component({
	selector: 'app-simulation',
	templateUrl: './simulation.component.html',
	styleUrl: './simulation.component.css'
})
export class SimulationComponent {
	simulationStats$ = this.store.select(selectDpsChanges).pipe(
		filter(change => !!change.characterId),
		switchMap(change => this.simulationService.simulate(change.characterId!))
	)

	constructor(
		private store: Store<CharacterModuleState>,
		private simulationService: SimulationService
	) {}
}
