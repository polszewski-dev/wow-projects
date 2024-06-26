import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Character } from '../model/Character';

@Injectable({
	providedIn: 'root'
})
export class CharacterService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/characters';

	constructor(private http: HttpClient) { }

	getCharacter(characterId: string) {
		return this.http.get<Character>(`${this.apiUrl}/${characterId}`);
	}
}
