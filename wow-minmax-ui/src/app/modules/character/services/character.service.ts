import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Character } from '../model/Character';
import { environment } from 'src/environments/environment';

@Injectable({
	providedIn: 'root'
})
export class CharacterService {
	private readonly apiUrl = environment.characterApiUrl;

	constructor(private http: HttpClient) { }

	getCharacter(characterId: string) {
		return this.http.get<Character>(`${this.apiUrl}/${characterId}`);
	}
}
