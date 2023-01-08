import { Location } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BuildId } from 'src/app/model/character/BuildId';
import { CharacterClass } from 'src/app/model/character/CharacterClass';
import { CharacterProfession } from 'src/app/model/character/CharacterProfession';
import { CreatureType } from 'src/app/model/character/CreatureType';
import { getMaxLevel, Phase } from 'src/app/model/character/Phase';
import { Profession } from 'src/app/model/character/Profession';
import { getProfessionSpecializations, ProfessionSpecialization } from 'src/app/model/character/ProfessionSpecialization';
import { Race } from 'src/app/model/character/Race';
import { ProfileInfo } from 'src/app/model/ProfileInfo';
import { ProfileService } from 'src/app/services/profile.service';

@Component({
	selector: 'app-new-profile',
	templateUrl: './new-profile.component.html',
	styleUrls: ['./new-profile.component.css']
})
export class NewProfileComponent {
	profileName = '';
	characterClass = CharacterClass.WARLOCK;
	characterClassOptions = [CharacterClass.WARLOCK].sort();
	race = Race.ORC;
	raceOptions = Object.values(Race).sort();
	level = 70;
	buildId = BuildId.DESTRO_SHADOW;
	buildOptions = [BuildId.DESTRO_SHADOW];
	profession1: CharacterProfession = {};
	profession2: CharacterProfession = {};
	professionOptions = Object.values(Profession).sort();
	specializationOptions = Object.values(ProfessionSpecialization).sort();
	enemyType = CreatureType.UNDEAD;
	enemyTypeOptions = Object.values(CreatureType).sort();
	phase = Phase.TBC_P5;
	phaseOptions = Object.values(Phase).sort();

	constructor(
		private profileService: ProfileService,
		private router: Router,
		private location: Location
	) {}

	onPhaseChange(): void {
		this.level = getMaxLevel(this.phase);
	}

	onProfessionChange(professionIdx: number): void {
		[this.profession1, this.profession2][professionIdx].specialization = undefined;
	}

	onAddClick(event: Event): void {
		event.preventDefault();

		if (!this.validate()) {
			alert('Some fields are missing');
			return;
		}

		let newProfile: ProfileInfo = {
			profileName: this.profileName,
			characterClass: this.characterClass,
			race: this.race,
			level: this.level,
			enemyType: this.enemyType,
			buildId: this.buildId,
			professions: [this.profession1, this.profession2],
			phase: this.phase
		};

		this.profileService.createProfile(newProfile).subscribe((createdProfile: ProfileInfo) => {
			this.router.navigate(['/edit-profile', createdProfile.profileId]);
		});
	}

	onCancelClick(event: Event): void {
		event.preventDefault();
		this.location.back();
	}

	private validate(): boolean {
		if (this.profileName === undefined || this.profileName === '') {
			return false;
		}

		if (this.profession1.profession === undefined) {
			return false;
		}

		if (this.profession2.profession === undefined) {
			return false;
		}

		if (this.profession1.profession === this.profession2.profession) {
			return false;
		}

		if (this.profession1.specialization === undefined && this.getSpecializationOptions(0).length > 0) {
			return false;
		}

		if (this.profession2.specialization === undefined && this.getSpecializationOptions(1).length > 0) {
			return false;
		}

		return true;
	}

	getSpecializationOptions(professionIdx: number): ProfessionSpecialization[] {
		const profession = [this.profession1, this.profession2][professionIdx].profession;
		return getProfessionSpecializations(profession);
	}
}
