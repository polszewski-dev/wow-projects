import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { NewProfileOptions } from '../model/NewProfileOptions';
import { CharacterSelectionOptions } from '../../character/model/CharacterSelectionOptions';
import { ProfileInfo } from '../model/ProfileInfo';

@Injectable({
	providedIn: 'root'
})
export class ProfileService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/profile';

	constructor(private http: HttpClient) { }

	getProfileList(): Observable<ProfileInfo[]> {
		return this.http.get<ProfileInfo[]>(`${this.apiUrl}/list`);
	}

	createProfile(profile: ProfileInfo): Observable<ProfileInfo> {
		return this.http.post<ProfileInfo>(this.apiUrl, profile);
	}

	getNewProfileOptions(): Observable<NewProfileOptions> {
		return this.http.get<NewProfileOptions>(`${this.apiUrl}/new/options`);
	}

	getCharacterSelectionOptions(profileId: string): Observable<CharacterSelectionOptions> {
		return this.http.get<CharacterSelectionOptions>(`${this.apiUrl}/${profileId}/char/selection/options`);
	}
}
