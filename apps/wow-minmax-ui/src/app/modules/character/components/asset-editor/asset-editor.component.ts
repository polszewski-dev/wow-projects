import { Component } from '@angular/core';
import { AssetStatus } from '../../model/Asset';
import { changeAssetStatus } from '../../state/character/character.actions';
import { selectAssetStatuses } from '../../state/character/character.selectors';

@Component({
	selector: 'app-asset-editor',
	templateUrl: './asset-editor.component.html',
	styleUrl: './asset-editor.component.css'
})
export class AssetEditorComponent {
	readonly selector = selectAssetStatuses;
	
	actionGenerator(playerId: string, assetStatus: AssetStatus) {
		return changeAssetStatus({ playerId, assetStatus });
	}
}
