import { Component, OnInit } from '@angular/core';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { ProfileService } from 'src/app/services/profile.service';

@Component({
	selector: 'app-main',
	templateUrl: './main.component.html',
	styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
	profileList: ProfileInfo[] = [];
	selectedProfile?: ProfileInfo;

	constructor(private profileService: ProfileService) {}

	ngOnInit(): void {
		this.profileService.getProfileList().subscribe(profileList => {
			this.profileList = profileList;
			this.selectMostRecentProfile();
		});
	}

	onProfileSelected(selectedProfile: ProfileInfo) {
		this.selectedProfile = selectedProfile;
	}

	private selectMostRecentProfile(): void {
		if (this.profileList.length === 0) {
			return;
		}
		this.selectedProfile = this.profileList.reduce((prev: ProfileInfo, current: ProfileInfo): ProfileInfo =>
			(prev.lastModified! > current.lastModified!) ? prev : current
		);
	}
}
