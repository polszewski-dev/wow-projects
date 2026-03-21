import { Component } from '@angular/core';
import { BuffStatus } from '../../model/buff/BuffStatus';
import { changeBuffStatus } from '../../state/character/character.actions';
import { selectBuffStatuses } from '../../state/character/character.selectors';

@Component({
	selector: 'app-buff-editor',
	templateUrl: './buff-editor.component.html',
	styleUrls: ['./buff-editor.component.css']
})
export class BuffEditorComponent {
	readonly selector = selectBuffStatuses;
	
	actionGenerator(playerId: string, buffStatus: BuffStatus) {
		return changeBuffStatus({ playerId, buffStatus });
	}
}
