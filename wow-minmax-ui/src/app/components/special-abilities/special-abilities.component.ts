import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { SpecialAbility } from 'src/app/model/stats/SpecialAbility';
import { StatsService } from 'src/app/services/stats.service';

@Component({
	selector: 'app-special-abilities',
	templateUrl: './special-abilities.component.html',
	styleUrls: ['./special-abilities.component.css']
})
export class SpecialAbilitiesComponent implements OnChanges {
	@Input() selectedProfile!: ProfileInfo;
	specialAbilities: SpecialAbility[] = [];

	constructor(private statsService: StatsService) {}

	ngOnChanges(changes: SimpleChanges) {
		if (!changes['selectedProfile']) {
			return;
		}
		this.statsService.getSpecialAbilities(this.selectedProfile.profileId).subscribe((specialAbilities: SpecialAbility[]) => {
			this.specialAbilities = specialAbilities;
		});
	}
}
