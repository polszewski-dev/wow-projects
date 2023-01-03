import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { SpellStats } from 'src/app/model/stats/SpellStats';
import { StatsService } from 'src/app/services/stats.service';

@Component({
	selector: 'app-spell-stats',
	templateUrl: './spell-stats.component.html',
	styleUrls: ['./spell-stats.component.css']
})
export class SpellStatsComponent implements OnChanges {
	@Input() selectedProfile!: ProfileInfo;
	spellStatsList: SpellStats[] = [];

	constructor(private statsService: StatsService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedProfile']) {
			return;
		}
		this.statsService.getSpellStats(this.selectedProfile.profileId).subscribe((spellStatsList: SpellStats[]) => {
			this.spellStatsList = spellStatsList;
		});
	}
}
