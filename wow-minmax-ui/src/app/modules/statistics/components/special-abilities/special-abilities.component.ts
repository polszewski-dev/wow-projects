import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { SpecialAbilityStats } from '../../model/SpecialAbilityStats';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-special-abilities',
	templateUrl: './special-abilities.component.html',
	styleUrls: ['./special-abilities.component.css']
})
export class SpecialAbilitiesComponent implements OnChanges {
	@Input() selectedCharacterId!: string;
	specialAbilityStatsList: SpecialAbilityStats[] = [];

	constructor(private statsService: StatsService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.statsService.getSpecialAbilities(this.selectedCharacterId).subscribe(specialAbilityStatsList => {
			this.specialAbilityStatsList = specialAbilityStatsList;
		});
	}
}
