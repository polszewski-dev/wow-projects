import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { StatisticsModule } from '../statistics/statistics.module';
import { CharacterSelectComponent } from './components/character-select/character-select.component';
import { NewProfileComponent } from './components/new-profile/new-profile.component';
import { ProfileEditorComponent } from './components/profile-editor/profile-editor.component';
import { ProfileSelectComponent } from './components/profile-select/profile-select.component';

@NgModule({
	declarations: [
		ProfileEditorComponent,
		ProfileSelectComponent,
		NewProfileComponent,
		CharacterSelectComponent,
	],
	imports: [
		RouterModule,
		StatisticsModule,
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
