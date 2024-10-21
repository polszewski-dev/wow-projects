import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Talent } from '../../shared/model/talent/Talent';
import { environment } from 'src/environments/environment';

@Injectable({
	providedIn: 'root'
})
export class TalentService {
	private readonly apiUrl = environment.talentApiUrl;

	constructor(private http: HttpClient) { }

	getTalents(characterId: string) {
		return this.http.get<Talent[]>(`${this.apiUrl}/${characterId}`);
	}
}
