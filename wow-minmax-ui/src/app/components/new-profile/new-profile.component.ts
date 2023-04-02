import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NewProfileOptions } from 'src/app/model/NewProfileOptions';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { CharacterClass } from 'src/app/model/character/CharacterClass';
import { Race } from 'src/app/model/character/Race';
import { ProfileService } from 'src/app/services/profile.service';

@Component({
	selector: 'app-new-profile',
	templateUrl: './new-profile.component.html',
	styleUrls: ['./new-profile.component.css']
})
export class NewProfileComponent implements OnInit {
	profileName = '';
	characterClass?: CharacterClass;
	race?: Race;
	newProfileOptions?: NewProfileOptions;

	constructor(
		private profileService: ProfileService,
		private router: Router,
		private location: Location
	) {}

	ngOnInit(): void {
		this.profileService.getNewProfileOptions().subscribe((newProfileOptions: NewProfileOptions) => {
			this.newProfileOptions = newProfileOptions;
		});
	}

	onAddClick(event: Event): void {
		event.preventDefault();

		if (!this.validate()) {
			alert('Some fields are missing');
			return;
		}

		let newProfile: ProfileInfo = {
			profileName: this.profileName,
			characterClass: this.characterClass!,
			race: this.race!,
		};

		this.profileService.createProfile(newProfile).subscribe((createdProfile: ProfileInfo) => {
			this.router.navigate(['/edit-profile', createdProfile.lastUsedCharacterId]);
		});
	}

	onCancelClick(event: Event): void {
		event.preventDefault();
		this.location.back();
	}

	onClassChange() {
		this.race = undefined;
	}

	private validate(): boolean {
		if (this.profileName === undefined || this.profileName === '') {
			return false;
		}

		if (this.characterClass === undefined) {
			return false;
		}

		if (this.race === undefined) {
			return false;
		}

		return true;
	}
}
