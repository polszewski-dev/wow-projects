import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { CharacterModule } from '../character/character.module';
import { SharedModule } from '../shared/shared.module';
import { StatisticsModule } from '../statistics/statistics.module';
import { NewProfileComponent } from './components/new-profile/new-profile.component';
import { ProfileEditorComponent } from './components/profile-editor/profile-editor.component';
import { ProfileSelectComponent } from './components/profile-select/profile-select.component';

@NgModule({
	declarations: [
		ProfileEditorComponent,
		ProfileSelectComponent,
		NewProfileComponent,
	],
	imports: [
		CommonModule,
		FormsModule,
		ReactiveFormsModule,
		RouterModule,
		NgbModule,
		SharedModule,
		CharacterModule,
		StatisticsModule,
	],
	exports: [
		ProfileEditorComponent,
		ProfileSelectComponent,
		NewProfileComponent,
	]
})
export class ProfileModule { }
