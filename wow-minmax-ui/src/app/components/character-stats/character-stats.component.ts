import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { CharacterStats } from 'src/app/model/stats/CharacterStats';
import { StatsService } from 'src/app/services/stats.service';

@Component({
	selector: 'app-character-stats',
	templateUrl: './character-stats.component.html',
	styleUrls: ['./character-stats.component.css']
})
export class CharacterStatsComponent implements OnChanges {
	@Input() selectedCharacterId!: string;
	characterStatsList: CharacterStats[] = [];

	constructor(private statsService: StatsService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.statsService.getCharacterStats(this.selectedCharacterId).subscribe((characterStatsList: CharacterStats[]) => {
			this.characterStatsList = characterStatsList;
		});
	}
}
