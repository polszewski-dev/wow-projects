import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Character } from '../model/Character';
import { ExclusiveFaction } from '../model/ExclusiveFaction';
import { ExclusiveFactionGroup } from '../model/ExclusiveFactionGroup';
import { Profession } from '../model/Profession';
import { ScriptInfo } from '../model/ScriptInfo';

@Injectable({
	providedIn: 'root'
})
export class CharacterService {
	private readonly apiUrl = environment.characterApiUrl;

	constructor(private http: HttpClient) { }

	getCharacter(characterId: string) {
		return this.http.get<Character>(`${this.apiUrl}/${characterId}`);
	}

	getAvailableProfessions(characterId: string) {
		return this.http.get<Profession[]>(`${this.apiUrl}/${characterId}/professions`);
	}

	changeProfession(characterId: string, professionIdx: number, profession: Profession) {
		return this.http.put<Character>(`${this.apiUrl}/${characterId}/professions/${professionIdx}`, profession);
	}

	getAvailableExclusiveFactions(characterId: string) {
		return this.http.get<ExclusiveFactionGroup[]>(`${this.apiUrl}/${characterId}/xfactions`);
	}

	changeExclusiveFaction(characterId: string, exclusiveFaction: ExclusiveFaction) {
		return this.http.put<void>(`${this.apiUrl}/${characterId}/xfactions`, exclusiveFaction);
	}

	getAvailableScripts(characterId: string) {
		return this.http.get<ScriptInfo[]>(`${this.apiUrl}/${characterId}/scripts`);
	}

	changeScript(characterId: string, script: ScriptInfo) {
		return this.http.put<Character>(`${this.apiUrl}/${characterId}/scripts`, script);
	}
}
