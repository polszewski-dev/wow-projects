import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
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

	constructor(
		private profileService: ProfileService,
		private route: ActivatedRoute,
		private location: Location
	) {}

	ngOnInit(): void {
		this.profileService.getProfileList().subscribe(profileList => {
			this.profileList = profileList;
			let profileToSelect = this.getProfileToSelect();
			if (profileToSelect) {
				this.onProfileSelected(profileToSelect);
			}
		});
	}

	onProfileSelected(selectedProfile: ProfileInfo): void {
		this.selectedProfile = selectedProfile;
		this.location.replaceState('/edit-profile/' + selectedProfile.profileId);
	}

	private getProfileToSelect(): ProfileInfo | undefined {
		if (this.route.snapshot.paramMap.has('id')) {
			const addressId = this.route.snapshot.paramMap.get('id');
			return this.profileList.filter(x => x.profileId === addressId)[0];
		}

		if (this.profileList.length === 0) {
			return undefined;
		}

		return this.profileList.reduce((prev: ProfileInfo, current: ProfileInfo): ProfileInfo =>
			(prev.lastModified! > current.lastModified!) ? prev : current
		);
	}
}
