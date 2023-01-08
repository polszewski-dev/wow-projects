import { Component, EventEmitter, Input, Output } from '@angular/core';
import { Phase } from 'src/app/model/character/Phase';
import { ProfileInfo } from 'src/app/model/ProfileInfo';

@Component({
	selector: 'app-profile-select',
	templateUrl: './profile-select.component.html',
	styleUrls: ['./profile-select.component.css']
})
export class ProfileSelectComponent {
	@Input() profileList: ProfileInfo[] = [];
	@Input() selectedProfile?: ProfileInfo;
	@Output() profileSelected = new EventEmitter<ProfileInfo>();

	onValueChange(): void {
		this.profileSelected.emit(this.selectedProfile);
	}

	sortProfiles = sortProfiles;
}

function sortProfiles(profileList: ProfileInfo[]): ProfileInfo[] {
	return profileList.sort((a: ProfileInfo, b: ProfileInfo): number => {
		const aPhaseIndex:number = Object.keys(Phase).indexOf(a.phase);
		const bPhaseIndex:number = Object.keys(Phase).indexOf(b.phase);

		let cmp = aPhaseIndex - bPhaseIndex;

		if (cmp !== 0) {
			return cmp;
		}

		return a.profileName.localeCompare(b.profileName)
	});
}
