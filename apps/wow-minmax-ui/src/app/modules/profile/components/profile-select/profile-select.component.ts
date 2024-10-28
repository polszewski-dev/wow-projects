import { Component, EventEmitter, Input, Output } from '@angular/core';
import { ProfileInfo } from '../../model/ProfileInfo';

@Component({
	selector: 'app-profile-select',
	templateUrl: './profile-select.component.html',
	styleUrls: ['./profile-select.component.css']
})
export class ProfileSelectComponent {
	@Input({ required: true }) profileList: ProfileInfo[] = [];
	@Input({ required: true }) selectedProfile!: ProfileInfo | null;
	@Output() profileSelected = new EventEmitter<ProfileInfo | null>();

	onProfileChange(profile: ProfileInfo | null) {
		this.profileSelected.emit(profile);
	}

	get sortedProfileList() {
		return sortProfiles(this.profileList);
	}
}

function sortProfiles(profileList: ProfileInfo[]) {
	return profileList.sort((a, b) => a.profileName.localeCompare(b.profileName));
}
