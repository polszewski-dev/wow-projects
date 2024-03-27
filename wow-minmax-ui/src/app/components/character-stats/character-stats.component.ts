import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { Character } from 'src/app/modules/character/model/Character';
import { CharacterStats } from 'src/app/model/stats/CharacterStats';
import { StatsService } from 'src/app/services/stats.service';

@Component({
	selector: 'app-character-stats',
	templateUrl: './character-stats.component.html',
	styleUrls: ['./character-stats.component.css']
})
export class CharacterStatsComponent implements OnChanges {
	@Input() selectedCharacter!: Character;
	characterStatsList: CharacterStats[] = [];

	constructor(private statsService: StatsService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacter']) {
			return;
		}
		this.statsService.getCharacterStats(this.selectedCharacter.characterId).subscribe((characterStatsList: CharacterStats[]) => {
			this.characterStatsList = characterStatsList;
		});
	}
}
