import { Component, Input, SimpleChanges } from '@angular/core';
import { Talent } from 'src/app/model/talent/Talent';
import { TalentService } from 'src/app/services/talent.service';

@Component({
	selector: 'app-talent-list',
	templateUrl: './talent-list.component.html',
	styleUrls: ['./talent-list.component.css']
})
export class TalentListComponent {
	@Input() selectedCharacterId!: string;

	talentList: Talent[] = [];

	constructor(private talentService: TalentService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.talentService.getTalents(this.selectedCharacterId).subscribe((talentList: Talent[]) => {
			this.talentList = talentList;
		});
	}
}
