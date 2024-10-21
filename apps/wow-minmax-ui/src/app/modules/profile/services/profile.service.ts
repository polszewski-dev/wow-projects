import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CharacterSelectionOptions } from '../../character/model/CharacterSelectionOptions';
import { NewProfileOptions } from '../model/NewProfileOptions';
import { ProfileInfo } from '../model/ProfileInfo';
import { environment } from 'src/environments/environment';

@Injectable({
	providedIn: 'root'
})
export class ProfileService {
	private readonly apiUrl = environment.profileApiUrl;

	constructor(private http: HttpClient) { }

	getProfileList() {
		return this.http.get<ProfileInfo[]>(`${this.apiUrl}`);
	}

	createProfile(profile: ProfileInfo) {
		return this.http.post<ProfileInfo>(this.apiUrl, profile);
	}

	getNewProfileOptions() {
		return this.http.get<NewProfileOptions>(`${this.apiUrl}/new-options`);
	}

	getCharacterSelectionOptions(profileId: string) {
		return this.http.get<CharacterSelectionOptions>(`${this.apiUrl}/${profileId}/char-selection-options`);
	}
}
