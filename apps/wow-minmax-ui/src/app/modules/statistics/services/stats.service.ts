import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { CharacterStats } from '../model/CharacterStats';
import { RotationStats } from '../model/RotationStats';
import { SpecialAbilityStats } from '../model/SpecialAbilityStats';
import { SpellStats } from '../model/SpellStats';
import { TalentStats } from '../model/TalentStats';

@Injectable({
	providedIn: 'root'
})
export class StatsService {
	private readonly apiUrl = environment.statsApiUrl;

	constructor(private http: HttpClient) { }

	getSpellStats(characterId: string) {
		return this.http.get<SpellStats[]>(`${this.apiUrl}/${characterId}/spell`);
	}

	getCharacterStats(characterId: string) {
		return this.http.get<CharacterStats[]>(`${this.apiUrl}/${characterId}/character`);
	}

	getSpecialAbilities(characterId: string) {
		return this.http.get<SpecialAbilityStats[]>(`${this.apiUrl}/${characterId}/special`);
	}

	getRotationStats(characterId: string) {
		return this.http.get<RotationStats>(`${this.apiUrl}/${characterId}/rotation`);
	}

	getTalentStats(characterId: string) {
		return this.http.get<TalentStats[]>(`${this.apiUrl}/${characterId}/talent`);
	}
}
