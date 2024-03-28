import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { parseCharacterId } from 'src/app/modules/character/model/CharacterId';
import { ProfileInfo } from 'src/app/modules/profile/model/ProfileInfo';
import { ProfileService } from 'src/app/modules/profile/services/profile.service';

@Component({
	selector: 'app-main',
	templateUrl: './main.component.html',
	styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
	profileList: ProfileInfo[] = [];
	selectedCharacterId?: string;

	constructor(
		private profileService: ProfileService,
		private route: ActivatedRoute,
		private location: Location
	) {}

	ngOnInit(): void {
		this.profileService.getProfileList().subscribe(profileList => {
			this.profileList = profileList;
			let characterToSelect = this.getCharacterToSelect();
			if (characterToSelect) {
				this.onCharacterSelected(characterToSelect);
			}
		});
	}

	onCharacterSelected(selectedCharacterId: string): void {
		this.selectedCharacterId = selectedCharacterId;
		this.location.replaceState('/edit-profile/' + this.selectedCharacterId);
	}

	private getCharacterToSelect(): string | undefined {
		if (this.route.snapshot.paramMap.has('id')) {
			const characterId = this.route.snapshot.paramMap.get('id');
			const profileId = parseCharacterId(characterId!).profileId;

			if (this.profileList.find(x => x.profileId === profileId)) {
				return characterId!;
			}
			return undefined;
		}

		if (this.profileList.length === 0) {
			return undefined;
		}

		const lastModifiedProfile = this.profileList.reduce((prev: ProfileInfo, current: ProfileInfo): ProfileInfo =>
			(prev.lastModified! > current.lastModified!) ? prev : current
		);

		return lastModifiedProfile?.lastUsedCharacterId;
	}
}
