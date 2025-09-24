import { NgModule } from '@angular/core';
import { CharacterModule } from '../character/character.module';
import { AbilityStatsComponent } from './components/ability-stats/ability-stats.component';
import { CharacterStatsComponent } from './components/character-stats/character-stats.component';
import { DpsBarComponent } from './components/dps-bar/dps-bar.component';
import { RacialListComponent } from './components/racial-list/racial-list.component';
import { SpecialAbilitiesComponent } from './components/special-abilities/special-abilities.component';
import { TalentStatsComponent } from './components/talent-stats/talent-stats.component';

@NgModule({
	declarations: [
		CharacterStatsComponent,
		DpsBarComponent,
		RacialListComponent,
		SpecialAbilitiesComponent,
		AbilityStatsComponent,
		TalentStatsComponent
	],
	imports: [
		CharacterModule,
	],
	exports: [
		CharacterStatsComponent,
		DpsBarComponent,
		RacialListComponent,
		SpecialAbilitiesComponent,
		AbilityStatsComponent,
		TalentStatsComponent
	]
})
export class StatisticsModule { }
