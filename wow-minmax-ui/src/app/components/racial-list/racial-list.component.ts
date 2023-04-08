import { Component, Input } from '@angular/core';
import { Character } from 'src/app/model/character/Character';

@Component({
	selector: 'app-racial-list',
	templateUrl: './racial-list.component.html',
	styleUrls: ['./racial-list.component.css']
})
export class RacialListComponent {
	@Input() selectedCharacter!: Character;
}
