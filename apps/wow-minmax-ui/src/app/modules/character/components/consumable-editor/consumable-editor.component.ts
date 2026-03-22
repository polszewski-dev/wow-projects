import { Component } from '@angular/core';
import { ConsumableStatus } from '../../model/Consumable';
import { changeConsumableStatus } from '../../state/character/character.actions';
import { selectConsumableStatuses } from '../../state/character/character.selectors';

@Component({
	selector: 'app-consumable-editor',
	templateUrl: './consumable-editor.component.html',
	styleUrls: ['./consumable-editor.component.css']
})
export class ConsumableEditorComponent {
	readonly selector = selectConsumableStatuses;
	
	actionGenerator(playerId: string, consumableStatus: ConsumableStatus) {
		return changeConsumableStatus({ playerId, consumableStatus });
	}
}
