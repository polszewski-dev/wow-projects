import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { AbilityStats } from '../model/AbilityStats';
import { CharacterStats } from '../model/CharacterStats';
import { RotationStats } from '../model/RotationStats';
import { SpecialAbilityStats } from '../model/SpecialAbilityStats';
import { TalentStats } from '../model/TalentStats';

@Injectable({
	providedIn: 'root'
})
export class StatsService {
	private readonly apiUrl = environment.statsApiUrl;

	constructor(private http: HttpClient) { }

	getAbilityStats(playerId: string) {
		return this.http.get<AbilityStats[]>(`${this.apiUrl}/${playerId}/ability`);
	}

	getCharacterStats(playerId: string) {
		return this.http.get<CharacterStats[]>(`${this.apiUrl}/${playerId}/character`);
	}

	getSpecialAbilities(playerId: string) {
		return this.http.get<SpecialAbilityStats[]>(`${this.apiUrl}/${playerId}/special`);
	}

	getRotationStats(playerId: string) {
		return this.http.get<RotationStats>(`${this.apiUrl}/${playerId}/rotation`);
	}

	getTalentStats(playerId: string) {
		return this.http.get<TalentStats[]>(`${this.apiUrl}/${playerId}/talent`);
	}
}
