import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Character } from '../model/character/Character';

@Injectable({
	providedIn: 'root'
})
export class CharacterService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/character';

	constructor(private http: HttpClient) { }

	getCharacter(characterId: string): Observable<Character> {
		return this.http.get<Character>(`${this.apiUrl}/${characterId}`);
	}
}
