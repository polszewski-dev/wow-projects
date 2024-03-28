import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Talent } from '../../shared/model/talent/Talent';

@Injectable({
	providedIn: 'root'
})
export class TalentService {
	private readonly apiUrl = 'http://localhost:8080/api/v1/talent';

	constructor(private http: HttpClient) { }

	getTalents(characterId: string) {
		return this.http.get<Talent[]>(`${this.apiUrl}/${characterId}/list`);
	}
}
