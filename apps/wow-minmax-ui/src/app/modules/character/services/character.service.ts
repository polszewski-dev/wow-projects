import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { Character } from '../model/Character';
import { Profession } from '../model/Profession';

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
}
