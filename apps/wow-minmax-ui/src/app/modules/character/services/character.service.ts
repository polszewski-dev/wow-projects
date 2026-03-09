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

	getCharacter(playerId: string) {
		return this.http.get<Character>(`${this.apiUrl}/${playerId}`);
	}

	getAvailableProfessions(playerId: string) {
		return this.http.get<Profession[]>(`${this.apiUrl}/${playerId}/professions`);
	}

	changeProfession(playerId: string, professionIdx: number, profession: Profession) {
		return this.http.put<Character>(`${this.apiUrl}/${playerId}/professions/${professionIdx}`, profession);
	}

	getAvailableExclusiveFactions(playerId: string) {
		return this.http.get<ExclusiveFactionGroup[]>(`${this.apiUrl}/${playerId}/xfactions`);
	}

	changeExclusiveFaction(playerId: string, exclusiveFaction: ExclusiveFaction) {
		return this.http.put<void>(`${this.apiUrl}/${playerId}/xfactions`, exclusiveFaction);
	}

	changeTalentLink(playerId: string, talentLink: string) {
		return this.http.put<Character>(`${this.apiUrl}/${playerId}/talents`, talentLink);
	}

	getAvailableScripts(playerId: string) {
		return this.http.get<ScriptInfo[]>(`${this.apiUrl}/${playerId}/scripts`);
	}

	changeScript(playerId: string, script: ScriptInfo) {
		return this.http.put<Character>(`${this.apiUrl}/${playerId}/scripts`, script);
	}
}
