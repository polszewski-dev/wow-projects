import { Location } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { parseCharacterId } from 'src/app/modules/character/model/CharacterId';

@Component({
	selector: 'app-main',
	templateUrl: './main.component.html',
	styleUrls: ['./main.component.css']
})
export class MainComponent implements OnInit {
	selectedProfileId: string | null = null;
	selectedCharacterId: string | null = null;

	constructor(
		private route: ActivatedRoute,
		private location: Location
	) {}

	ngOnInit(): void {
		if (this.route.snapshot.paramMap.has('id')) {
			this.selectedCharacterId = this.route.snapshot.paramMap.get('id');
			this.selectedProfileId = parseCharacterId(this.selectedCharacterId!).profileId;
		}
	}

	onProfileSelected(selectedProfileId: string | null) {
		this.selectedCharacterId = null;
		this.selectedProfileId = selectedProfileId;
	}

	onCharacterSelected(selectedCharacterId: string) {
		this.selectedCharacterId = selectedCharacterId;
		this.location.replaceState('/edit-profile/' + this.selectedCharacterId);
	}
}
