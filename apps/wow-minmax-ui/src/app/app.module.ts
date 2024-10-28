import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { EffectsModule } from '@ngrx/effects';
import { StoreModule } from '@ngrx/store';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MainComponent } from './components/main/main.component';
import { CharacterEffects } from './modules/character/state/character/character.effects';
import { characterReducer } from './modules/character/state/character/character.reducer';
import { EquipmentOptionsEffects } from './modules/character/state/equipment-options/equipment-options.effects';
import { equipmentOptionsReducer } from './modules/character/state/equipment-options/equipment-options.reducer';
import { UpgradesEffects } from './modules/character/state/upgrades/upgrades.effects';
import { upgradesReducer } from './modules/character/state/upgrades/upgrades.reducer';
import { ProfileModule } from './modules/profile/profile.module';
import { ProfileEffects } from './modules/profile/state/profile.effects';
import { profileReducer } from './modules/profile/state/profile.reducer';

@NgModule({
	declarations: [
		AppComponent,
		MainComponent,
	],
	imports: [
		BrowserModule,
		AppRoutingModule,
		ProfileModule,
		StoreModule.forRoot({
			profile: profileReducer,
			character: characterReducer,
			equipmentOptions: equipmentOptionsReducer,
			upgrades: upgradesReducer
		}),
		EffectsModule.forRoot([
			ProfileEffects,
			CharacterEffects,
			EquipmentOptionsEffects,
			UpgradesEffects
		]),
	],
	providers: [provideHttpClient(withInterceptorsFromDi())],
	bootstrap: [AppComponent]
})
export class AppModule { }
