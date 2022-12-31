import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
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
}
