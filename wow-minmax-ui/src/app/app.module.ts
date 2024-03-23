import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainComponent } from './components/main/main.component';
import { NewProfileComponent } from './components/new-profile/new-profile.component';
import { ProfileEditorComponent } from './components/profile-editor/profile-editor.component';
import { ProfileSelectComponent } from './components/profile-select/profile-select.component';
import { CharacterModule } from './modules/character/character.module';
import { SharedModule } from './modules/shared/shared.module';
import { StatisticsModule } from './modules/statistics/statistics.module';

@NgModule({
	declarations: [
		AppComponent,
		ProfileEditorComponent,
		ProfileSelectComponent,
		NewProfileComponent,
		MainComponent
	],
	imports: [
		BrowserModule,
		AppRoutingModule,
		HttpClientModule,
		FormsModule,
		NgbModule,
		SharedModule,
		CharacterModule,
		StatisticsModule
	],
	providers: [],
	bootstrap: [AppComponent]
})
export class AppModule { }
