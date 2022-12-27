import { Component } from '@angular/core';
import { ProfileInfo } from 'src/app/model/ProfileInfo';

@Component({
	selector: 'app-profile-editor',
	templateUrl: './profile-editor.component.html',
	styleUrls: ['./profile-editor.component.css']
})
export class ProfileEditorComponent {
	selectedProfile?: ProfileInfo;

	onProfileSelected(selectedProfile: ProfileInfo): void {
		this.selectedProfile = selectedProfile;
	}
}
