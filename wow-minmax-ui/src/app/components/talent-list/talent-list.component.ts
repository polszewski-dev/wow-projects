import { Component, Input, SimpleChanges } from '@angular/core';
import { Talent } from 'src/app/model/talent/Talent';
import { TalentService } from 'src/app/services/talent.service';

@Component({
	selector: 'app-talent-list',
	templateUrl: './talent-list.component.html',
	styleUrls: ['./talent-list.component.css']
})
export class TalentListComponent {
	@Input() selectedProfileId!: string;

	talentList: Talent[] = [];

	constructor(private talentService: TalentService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedProfileId']) {
			return;
		}
		this.talentService.getTalents(this.selectedProfileId).subscribe((talentList: Talent[]) => {
			this.talentList = talentList;
		});
	}
}
