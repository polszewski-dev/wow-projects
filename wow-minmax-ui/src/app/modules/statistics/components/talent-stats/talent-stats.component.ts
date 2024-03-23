import { Component, Input, SimpleChanges } from '@angular/core';
import { TalentStats } from '../../model/TalentStats';
import { StatsService } from '../../services/stats.service';

@Component({
	selector: 'app-talent-stats',
	templateUrl: './talent-stats.component.html',
	styleUrls: ['./talent-stats.component.css']
})
export class TalentStatsComponent {
	@Input() selectedCharacterId!: string;

	talentStats: TalentStats[] = [];

	constructor(private statsService: StatsService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.statsService.getTalentStats(this.selectedCharacterId).subscribe(talentStats => {
			this.talentStats = talentStats;
		});
	}
}
