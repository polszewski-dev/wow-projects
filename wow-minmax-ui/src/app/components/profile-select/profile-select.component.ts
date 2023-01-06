import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Phase } from 'src/app/model/character/Phase';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { ProfileService } from 'src/app/services/profile.service';

@Component({
	selector: 'app-profile-select',
	templateUrl: './profile-select.component.html',
	styleUrls: ['./profile-select.component.css']
})
export class ProfileSelectComponent implements OnInit {
	profileList: ProfileInfo[] = [];
	selectedProfile?: ProfileInfo;
	@Output() profileSelected = new EventEmitter<ProfileInfo>();

	constructor(private profileService: ProfileService) {}

	ngOnInit(): void {
		this.profileService.getProfileList().subscribe(profileList => {
			this.profileList = sortProfiles(profileList);
			this.selectMostRecentProfile();
		});
	}

	onValueChange(): void {
		this.profileSelected.emit(this.selectedProfile);
	}

	onNewProfileClick(): void {
		console.log('New profile clicked');
	}

	private selectMostRecentProfile(): void {
		if (this.profileList.length === 0) {
			return;
		}
		this.selectedProfile = this.profileList.reduce((prev: ProfileInfo, current: ProfileInfo): ProfileInfo =>
			(prev.lastModified! > current.lastModified!) ? prev : current
		);
		this.onValueChange();
	}
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
