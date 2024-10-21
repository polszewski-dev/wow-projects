import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ProfileInfo } from '../../model/ProfileInfo';
import { ProfileService } from '../../services/profile.service';

@Component({
	selector: 'app-profile-select',
	templateUrl: './profile-select.component.html',
	styleUrls: ['./profile-select.component.css']
})
export class ProfileSelectComponent implements OnInit {
	@Input({ required: true }) selectedProfileId!: string | null;
	@Output() profileSelected = new EventEmitter<string | null>();

	profileList: ProfileInfo[] = [];

	constructor(private profileService: ProfileService) {}

	ngOnInit(): void {
		this.profileService.getProfileList().subscribe(profileList => {
			this.profileList = profileList;

			const profileId = this.pickProfile();

			if (this.selectedProfileId !== profileId) {
				this.selectedProfileId = profileId;
				this.onProfileChange(profileId);
			}
		});
	}

	onProfileChange(profileId: string | null) {
		this.profileSelected.emit(profileId);
	}

	private pickProfile() {
		if (this.selectedProfileId && this.profileList.some(x => x.profileId === this.selectedProfileId)) {
			return this.selectedProfileId;
		}
		return this.getLastModifiedProfile()?.profileId || null;
	}

	private getLastModifiedProfile() {
		if (this.profileList.length === 0) {
			return null;
		}
		return this.profileList.reduce((prev: ProfileInfo, current: ProfileInfo) =>
			(prev.lastModified! > current.lastModified!) ? prev : current
		);
	}

	get sortedProfileList() {
		return sortProfiles(this.profileList);
	}
}

function sortProfiles(profileList: ProfileInfo[]) {
	return profileList.sort((a, b) => a.profileName.localeCompare(b.profileName));
}
