import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { DropdownSelectValueFormatter } from 'src/app/modules/shared/components/dropdown-select/dropdown-select.component';
import { getMediumIcon } from 'src/app/modules/shared/util/Icon';
import { CharacterClass } from '../../../shared/model/character/CharacterClass';
import { Race } from '../../../shared/model/character/Race';
import { NewProfileOptions } from '../../model/NewProfileOptions';
import { ProfileInfo } from '../../model/ProfileInfo';
import { ProfileService } from '../../services/profile.service';

@Component({
	selector: 'app-new-profile',
	templateUrl: './new-profile.component.html',
	styleUrls: ['./new-profile.component.css']
})
export class NewProfileComponent implements OnInit {
	readonly form = new FormGroup({
		profileName: new FormControl<string | null>(null, Validators.required),
		characterClass: new FormControl<CharacterClass | null>(null, Validators.required),
		race: new FormControl<Race | null>(null, Validators.required)
	});

	newProfileOptions!: NewProfileOptions;

	constructor(
		private profileService: ProfileService,
		private router: Router,
		private location: Location
	) {}

	ngOnInit(): void {
		this.profileService.getNewProfileOptions().subscribe(newProfileOptions => {
			this.newProfileOptions = newProfileOptions;
		});

		this.characterClassControl.valueChanges
			.subscribe(newCharacterClass => this.onClassChange(newCharacterClass));
	}

	onAddClick() {
		let newProfile: ProfileInfo = {
			profileName: this.profileName!,
			characterClass: this.characterClass!,
			race: this.race!,
		};

		this.profileService.createProfile(newProfile).subscribe(createdProfile => {
			this.router.navigate(['/edit-profile', createdProfile.profileId]);
		});
	}

	onCancelClick(event: Event) {
		event.preventDefault();
		this.location.back();
	}

	private onClassChange(newCharacterClass: CharacterClass | null) {
		if (!this.shouldKeepSelectedRace(newCharacterClass)) {
			this.form.patchValue({ race: null });
		}
	}

	private shouldKeepSelectedRace(newCharacterClass: CharacterClass | null) {
		const currentRace = this.race;

		return newCharacterClass?.races.some(x => x.id === currentRace?.id)
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

	readonly characterClassFormatter = new CharacterClassFormatter();
	readonly raceFormatter = new RaceFormatter();
}

class CharacterClassFormatter implements DropdownSelectValueFormatter<CharacterClass> {
	formatElement(value: CharacterClass) {
		return `
			<img src="${getMediumIcon(value.icon)}"/>
			<span>&nbsp;${value.name}</span>
		`;
	}

	formatSelection(value: CharacterClass) {
		return this.formatElement(value);
	}

	formatTooltip(value?: CharacterClass) {
		return "";
	}

	trackKey(value: CharacterClass) {
		return value.id;
	}
}

class RaceFormatter implements DropdownSelectValueFormatter<Race> {
	formatElement(value: Race) {
		return `
			<img src="${getMediumIcon(value.icon)}"/>
			<span>&nbsp;${value.name}</span>
		`;
	}

	formatSelection(value: Race) {
		return this.formatElement(value);
	}

	formatTooltip(value?: Race) {
		return "";
	}

	trackKey(value: Race) {
		return value.id;
	}
}
