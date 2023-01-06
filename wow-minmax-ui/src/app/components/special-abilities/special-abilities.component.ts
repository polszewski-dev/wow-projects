import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { SpecialAbilityStats } from 'src/app/model/stats/SpecialAbilityStats';
import { StatsService } from 'src/app/services/stats.service';

@Component({
	selector: 'app-special-abilities',
	templateUrl: './special-abilities.component.html',
	styleUrls: ['./special-abilities.component.css']
})
export class SpecialAbilitiesComponent implements OnChanges {
	@Input() selectedProfileId!: string;
	specialAbilityStatsList: SpecialAbilityStats[] = [];

	constructor(private statsService: StatsService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedProfileId']) {
			return;
		}
		this.statsService.getSpecialAbilities(this.selectedProfileId).subscribe((specialAbilityStatsList: SpecialAbilityStats[]) => {
			this.specialAbilityStatsList = specialAbilityStatsList;
		});
	}
}
