import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { CharacterStats } from 'src/app/model/stats/CharacterStats';
import { StatsService } from 'src/app/services/stats.service';

@Component({
	selector: 'app-character-stats',
	templateUrl: './character-stats.component.html',
	styleUrls: ['./character-stats.component.css']
})
export class CharacterStatsComponent implements OnChanges {
	@Input() selectedProfile!: ProfileInfo;
	characterStatsList: CharacterStats[] = [];

	constructor(private statsService: StatsService) {}

	ngOnChanges(changes: SimpleChanges) {
		if (!changes['selectedProfile']) {
			return;
		}
		this.statsService.getCharacterStats(this.selectedProfile.profileId).subscribe((characterStatsList: CharacterStats[]) => {
			this.characterStatsList = characterStatsList;
		});
	}
}