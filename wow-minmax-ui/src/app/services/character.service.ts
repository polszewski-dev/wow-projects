import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Racial } from '../model/character/Racial';

@Injectable({
	providedIn: 'root'
})
export class CharacterService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/character';

	constructor(private http: HttpClient) { }

	getRacials(characterId: string): Observable<Racial[]> {
		return this.http.get<Racial[]>(`${this.apiUrl}/${characterId}/racial/list`);
	}
}
