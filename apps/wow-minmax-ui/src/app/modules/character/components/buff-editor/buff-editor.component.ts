import { Component } from '@angular/core';
import { BuffListType } from '../../model/buff/BuffListType';
import { BuffStatus } from '../../model/buff/BuffStatus';
import { changeBuffStatus } from '../../state/character/character.actions';
import { selectBuffList } from '../../state/character/character.selectors';

@Component({
	selector: 'app-buff-editor',
	templateUrl: './buff-editor.component.html',
	styleUrls: ['./buff-editor.component.css']
})
export class BuffEditorComponent {
	readonly selector = selectBuffList(BuffListType.CHARACTER_BUFF);
	
	actionGenerator(playerId: string, buffStatus: BuffStatus) {
		return changeBuffStatus({ playerId, buffListType: BuffListType.CHARACTER_BUFF, buffStatus });
	}
}
