import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CharacterStats } from '../model/stats/CharacterStats';
import { SpecialAbilityStats } from '../model/stats/SpecialAbilityStats';
import { SpellStats } from '../model/stats/SpellStats';
import { RotationStats } from '../model/character/RotationStats';

@Injectable({
	providedIn: 'root'
})
export class StatsService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/stats';

	constructor(private http: HttpClient) { }

	getSpellStats(characterId: string): Observable<SpellStats[]> {
		return this.http.get<SpellStats[]>(`${this.apiUrl}/${characterId}/spell`);
	}

	getCharacterStats(characterId: string): Observable<CharacterStats[]> {
		return this.http.get<CharacterStats[]>(`${this.apiUrl}/${characterId}/character`);
	}

	getSpecialAbilities(characterId: string): Observable<SpecialAbilityStats[]> {
		return this.http.get<SpecialAbilityStats[]>(`${this.apiUrl}/${characterId}/special`);
	}

	getRotationStats(characterId: string): Observable<RotationStats> {
		return this.http.get<RotationStats>(`${this.apiUrl}/${characterId}/rotation/stats`);
	}
}
