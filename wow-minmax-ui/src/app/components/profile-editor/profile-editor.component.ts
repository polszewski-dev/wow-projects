import { Component } from '@angular/core';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { StatsService } from 'src/app/services/stats.service';

@Component({
	selector: 'app-profile-editor',
	templateUrl: './profile-editor.component.html',
	styleUrls: ['./profile-editor.component.css']
})
export class ProfileEditorComponent {
	selectedProfile?: ProfileInfo;
	dps?: number;
	previousDps?: number;

	constructor(private statsService: StatsService) {}

	onProfileSelected(selectedProfile: ProfileInfo): void {
		this.selectedProfile = selectedProfile;
		this.dps = undefined;
		this.updateDps();
	}

	onEquipmentChanged() {
		this.updateDps();
	}

	onBuffsChanged() {
		this.updateDps();	
	}

	updateDps() {
		this.statsService.getSpellDps(this.selectedProfile!.profileId).subscribe((dps: number) => {
			this.previousDps = this.dps;
			this.dps = dps;
		})
	}
}
