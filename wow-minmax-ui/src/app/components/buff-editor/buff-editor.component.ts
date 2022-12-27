import { Component, Input } from '@angular/core';
import { ProfileInfo } from 'src/app/model/ProfileInfo';

@Component({
	selector: 'app-buff-editor',
	templateUrl: './buff-editor.component.html',
	styleUrls: ['./buff-editor.component.css']
})
export class BuffEditorComponent {
	@Input() selectedProfile?: ProfileInfo;
}
