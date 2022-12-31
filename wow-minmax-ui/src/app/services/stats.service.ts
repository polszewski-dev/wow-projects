import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CharacterStats } from '../model/stats/CharacterStats';
import { SpecialAbility } from '../model/stats/SpecialAbility';
import { SpellStats } from '../model/stats/SpellStats';

@Injectable({
	providedIn: 'root'
})
export class StatsService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/stats';

	constructor(private http: HttpClient) { }

	getSpellStats(profileId: string): Observable<SpellStats[]> {
		return this.http.get<SpellStats[]>(`${this.apiUrl}/${profileId}/spell`);
	}

	getCharacterStats(profileId: string): Observable<CharacterStats[]> {
		return this.http.get<CharacterStats[]>(`${this.apiUrl}/${profileId}/character`);
	}

	getSpecialAbilities(profileId: string): Observable<SpecialAbility[]> {
		return this.http.get<SpecialAbility[]>(`${this.apiUrl}/${profileId}/special`);
	}
}
