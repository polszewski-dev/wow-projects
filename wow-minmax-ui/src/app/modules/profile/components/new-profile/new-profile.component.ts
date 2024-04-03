import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CharacterClass } from '../../../shared/model/character/CharacterClass';
import { Race } from '../../../shared/model/character/Race';
import { NewProfileOptions } from '../../model/NewProfileOptions';
import { ProfileInfo } from '../../model/ProfileInfo';
import { ProfileService } from '../../services/profile.service';
import { FormBuilder, Validators } from '@angular/forms';

@Component({
	selector: 'app-new-profile',
	templateUrl: './new-profile.component.html',
	styleUrls: ['./new-profile.component.css']
})
export class NewProfileComponent implements OnInit {
	form = this.formBuilder.group({
		profileName: [ '', Validators.required ],
		characterClass: [ null as unknown as CharacterClass, Validators.required ],
		race: [ null as unknown as Race, Validators.required ]
	});

	newProfileOptions!: NewProfileOptions;

	constructor(
		private profileService: ProfileService,
		private router: Router,
		private location: Location,
		private formBuilder: FormBuilder
	) {}

	ngOnInit(): void {
		this.profileService.getNewProfileOptions().subscribe(newProfileOptions => {
			this.newProfileOptions = newProfileOptions;
		});
	}

	onAddClick() {
		let newProfile: ProfileInfo = {
			profileName: this.profileName!,
			characterClass: this.characterClass!,
			race: this.race!,
		};

		this.profileService.createProfile(newProfile).subscribe(createdProfile => {
			this.router.navigate(['/edit-profile', createdProfile.lastUsedCharacterId]);
		});
	}

	onCancelClick(event: Event) {
		event.preventDefault();
		this.location.back();
	}

	onClassChange() {
		this.form.patchValue({ race: null });
	}

	get profileName() {
		return this.form.value.profileName;
	}

	get characterClass() {
		return this.form.value.characterClass;
	}

	get race() {
		return this.form.value.race;
	}

	get profileNameControl() {
		return this.form.controls.profileName;
	}

	get characterClassControl() {
		return this.form.controls.characterClass;
	}

	get raceControl() {
		return this.form.controls.race;
	}
}
