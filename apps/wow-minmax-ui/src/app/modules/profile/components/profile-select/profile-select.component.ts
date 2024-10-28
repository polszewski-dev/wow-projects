import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Store } from '@ngrx/store';
import { map, tap } from 'rxjs';
import { ProfileInfo } from '../../model/ProfileInfo';
import { ProfileModuleState } from '../../state/profile-module.state';
import { loadProfiles, selectProfile } from '../../state/profile.actions';
import { selectProfiles } from '../../state/profile.selectors';

@Component({
	selector: 'app-profile-select',
	templateUrl: './profile-select.component.html',
	styleUrls: ['./profile-select.component.css']
})
export class ProfileSelectComponent implements OnInit {
	selectedProfile: ProfileInfo | null = null;

	profileList$ = this.store.select(selectProfiles).pipe(
		map(profiles => sortProfiles(profiles)),
		tap(profiles => {
			const selectedProfile = this.pickProfile(profiles);
			this.selectedProfile = selectedProfile;
			if (selectedProfile) {
				this.store.dispatch(selectProfile({ selectedProfile }));
			}
		})
	);

	constructor(
		private store: Store<ProfileModuleState>,
		private route: ActivatedRoute
	) {}

	ngOnInit(): void {
		this.store.dispatch(loadProfiles());
	}

	onProfileChange(selectedProfile: ProfileInfo) {
		this.store.dispatch(selectProfile({ selectedProfile }));
	}

	private pickProfile(profiles: ProfileInfo[]) {
		if (this.route.snapshot.paramMap.has('id')) {
			const profileIdFromUrl = this.route.snapshot.paramMap.get('id');
			const profile = profiles.find(x => x.profileId === profileIdFromUrl);

			if (profile) {
				return profile;
			}
		}

		return this.getLastModifiedProfile(profiles);
	}

	private getLastModifiedProfile(profiles: ProfileInfo[]) {
		if (profiles.length === 0) {
			return null;
		}
		return profiles.reduce((prev: ProfileInfo, current: ProfileInfo) =>
			(prev.lastModified! > current.lastModified!) ? prev : current
		);
	}
}

function sortProfiles(profileList: ProfileInfo[]) {
	return [...profileList].sort((a, b) => a.profileName.localeCompare(b.profileName));
}
