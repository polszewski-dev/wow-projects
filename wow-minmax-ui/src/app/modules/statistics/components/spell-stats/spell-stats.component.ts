import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { SpellStats } from '../../model/SpellStats';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-spell-stats',
	templateUrl: './spell-stats.component.html',
	styleUrls: ['./spell-stats.component.css']
})
export class SpellStatsComponent implements OnChanges {
	@Input() selectedCharacterId!: string;
	spellStatsList: SpellStats[] = [];

	constructor(private statsService: StatsService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.statsService.getSpellStats(this.selectedCharacterId).subscribe(spellStatsList => {
			this.spellStatsList = spellStatsList;
		});
	}
}
