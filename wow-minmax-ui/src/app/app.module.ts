import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BuffEditorComponent } from './components/buff-editor/buff-editor.component';
import { DropdownSelectComponent } from './components/dropdown-select/dropdown-select.component';
import { EquipmentEditorComponent } from './components/equipment-editor/equipment-editor.component';
import { EquipmentSlotEditorComponent } from './components/equipment-slot-editor/equipment-slot-editor.component';
import { ProfileEditorComponent } from './components/profile-editor/profile-editor.component';
import { ProfileSelectComponent } from './components/profile-select/profile-select.component';
import { SpellStatsComponent } from './components/spell-stats/spell-stats.component';
import { IconPipe } from './pipes/icon.pipe';
import { BlankZeroPipe } from './pipes/blank-zero.pipe';
import { CharacterStatsComponent } from './components/character-stats/character-stats.component';
import { SpecialAbilitiesComponent } from './components/special-abilities/special-abilities.component';

@NgModule({
	declarations: [
		AppComponent,
		ProfileEditorComponent,
		ProfileSelectComponent,
		EquipmentEditorComponent,
		BuffEditorComponent,
		EquipmentSlotEditorComponent,
		DropdownSelectComponent,
		SpellStatsComponent,
		IconPipe,
		BlankZeroPipe,
		CharacterStatsComponent,
		SpecialAbilitiesComponent
	],
	imports: [
		BrowserModule,
		AppRoutingModule,
		HttpClientModule,
		FormsModule,
		NgbModule
	],
	providers: [],
	bootstrap: [AppComponent]
})
export class AppModule { }
