import { Component, Input, SimpleChanges } from '@angular/core';
import { Racial } from 'src/app/model/character/Racial';
import { CharacterService } from 'src/app/services/character.service';

@Component({
	selector: 'app-racial-list',
	templateUrl: './racial-list.component.html',
	styleUrls: ['./racial-list.component.css']
})
export class RacialListComponent {
	@Input() selectedCharacterId!: string;

	racialList: Racial[] = [];

	constructor(private characterService: CharacterService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}
		this.characterService.getRacials(this.selectedCharacterId).subscribe((racialList: Racial[]) => {
			this.racialList = racialList;
		});
	}
}
