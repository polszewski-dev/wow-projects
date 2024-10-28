import { NgModule } from '@angular/core';
import { CharacterModule } from '../character/character.module';
import { SharedModule } from '../shared/shared.module';
import { CharacterStatsComponent } from './components/character-stats/character-stats.component';
import { DpsBarComponent } from './components/dps-bar/dps-bar.component';
import { RacialListComponent } from './components/racial-list/racial-list.component';
import { SpecialAbilitiesComponent } from './components/special-abilities/special-abilities.component';
import { SpellStatsComponent } from './components/spell-stats/spell-stats.component';
import { TalentStatsComponent } from './components/talent-stats/talent-stats.component';

@NgModule({
	declarations: [
		CharacterStatsComponent,
		DpsBarComponent,
		RacialListComponent,
		SpecialAbilitiesComponent,
		SpellStatsComponent,
		TalentStatsComponent
	],
	imports: [
		CharacterModule,
	],
	exports: [
		CharacterModule,
		CharacterStatsComponent,
		DpsBarComponent,
		RacialListComponent,
		SpecialAbilitiesComponent,
		SpellStatsComponent,
		TalentStatsComponent
	]
})
export class StatisticsModule { }
