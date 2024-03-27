import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CharacterStatsComponent } from './components/character-stats/character-stats.component';
import { DpsBarComponent } from './components/dps-bar/dps-bar.component';
import { MainComponent } from './components/main/main.component';
import { NewProfileComponent } from './components/new-profile/new-profile.component';
import { ProfileEditorComponent } from './components/profile-editor/profile-editor.component';
import { ProfileSelectComponent } from './components/profile-select/profile-select.component';
import { RacialListComponent } from './components/racial-list/racial-list.component';
import { SpecialAbilitiesComponent } from './components/special-abilities/special-abilities.component';
import { SpellStatsComponent } from './components/spell-stats/spell-stats.component';
import { TalentListComponent } from './components/talent-list/talent-list.component';
import { CharacterModule } from './modules/character/character.module';
import { SharedModule } from './modules/shared/shared.module';

@NgModule({
	declarations: [
		AppComponent,
		ProfileEditorComponent,
		ProfileSelectComponent,
		SpellStatsComponent,
		CharacterStatsComponent,
		SpecialAbilitiesComponent,
		DpsBarComponent,
		NewProfileComponent,
		MainComponent,
		RacialListComponent,
		TalentListComponent
	],
	imports: [
		BrowserModule,
		AppRoutingModule,
		HttpClientModule,
		FormsModule,
		NgbModule,
		SharedModule,
		CharacterModule
	],
	providers: [],
	bootstrap: [AppComponent]
})
export class AppModule { }
