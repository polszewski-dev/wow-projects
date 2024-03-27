import { Component, Input } from '@angular/core';
import { Character } from 'src/app/modules/character/model/Character';

@Component({
	selector: 'app-racial-list',
	templateUrl: './racial-list.component.html',
	styleUrls: ['./racial-list.component.css']
})
export class RacialListComponent {
	@Input() selectedCharacter!: Character;
}
