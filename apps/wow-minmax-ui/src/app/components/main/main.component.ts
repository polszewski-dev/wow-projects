import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ProfileService } from 'src/app/modules/profile/services/profile.service';
import { ProfileInfo } from '../../modules/profile/model/ProfileInfo';

@Component({
	selector: 'app-main',
	templateUrl: './main.component.html',
	styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
	profileList: ProfileInfo[] = [];
	selectedProfile: ProfileInfo | null = null;
	selectedCharacterId: string | null = null;

	constructor(
		private profileService: ProfileService,
		private route: ActivatedRoute,
		private location: Location
	) {}

	ngOnInit(): void {
		this.profileService.getProfileList().subscribe(profileList => {
			this.profileList = profileList;
			this.selectProfile(this.pickProfile());
		});
	}

	onProfileSelected(selectedProfile: ProfileInfo | null) {
		this.selectProfile(selectedProfile);
	}

	onCharacterSelected(selectedCharacterId: string) {
		this.selectedCharacterId = selectedCharacterId;
	}

	private selectProfile(selectedProfile: ProfileInfo | null) {
		this.selectedProfile = selectedProfile;
		this.selectedCharacterId = this.selectedProfile?.lastUsedCharacterId || null;

		if (this.selectedProfile) {
			this.location.replaceState('/edit-profile/' + this.selectedProfile.profileId);
		} else {
			this.location.replaceState('/');
		}
	}

	private pickProfile() {
		if (this.route.snapshot.paramMap.has('id')) {
			const profileIdFromUrl = this.route.snapshot.paramMap.get('id');
			const profile = this.profileList.find(x => x.profileId === profileIdFromUrl);

			if (profile) {
				return profile;
			}
		}

		return this.getLastModifiedProfile();
	}

	private getLastModifiedProfile() {
		if (this.profileList.length === 0) {
			return null;
		}
		return this.profileList.reduce((prev: ProfileInfo, current: ProfileInfo) =>
			(prev.lastModified! > current.lastModified!) ? prev : current
		);
	}
}
