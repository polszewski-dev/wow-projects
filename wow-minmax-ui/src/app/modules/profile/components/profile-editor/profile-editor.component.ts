import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';
import { BuffListType } from '../../../character/model/buff/BuffListType';
import { CharacterStateService } from '../../../character/services/character-state.service';

@Component({
	selector: 'app-profile-editor',
	templateUrl: './profile-editor.component.html',
	styleUrls: ['./profile-editor.component.css']
})
export class ProfileEditorComponent implements OnChanges {
	@Input() selectedCharacterId!: string;

	dataLoaded$ = this.characterStateService.character$;

	readonly CHARACTER_BUFF = BuffListType.CHARACTER_BUFF;
	readonly TARGET_DEBUFF = BuffListType.TARGET_DEBUFF;

	constructor(private characterStateService: CharacterStateService) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (!changes['selectedCharacterId']) {
			return;
		}

		this.characterStateService.selectCharacter(this.selectedCharacterId);
	}
}
