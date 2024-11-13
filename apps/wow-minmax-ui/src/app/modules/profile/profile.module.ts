import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { StatisticsModule } from '../statistics/statistics.module';
import { CharacterSelectComponent } from './components/character-select/character-select.component';
import { NewProfileComponent } from './components/new-profile/new-profile.component';
import { ProfileEditorComponent } from './components/profile-editor/profile-editor.component';
import { ProfileSelectComponent } from './components/profile-select/profile-select.component';
import { CharacterModule } from '../character/character.module';
import { SimulationModule } from '../simulation/simulation.module';

@NgModule({
	declarations: [
		ProfileEditorComponent,
		ProfileSelectComponent,
		NewProfileComponent,
		CharacterSelectComponent,
	],
	imports: [
		RouterModule,
		CharacterModule,
		StatisticsModule,
		SimulationModule,
	],
	exports: [
		RouterModule,
		StatisticsModule,
		ProfileEditorComponent,
		ProfileSelectComponent,
		NewProfileComponent,
		CharacterSelectComponent,
	]
})
export class ProfileModule { }
