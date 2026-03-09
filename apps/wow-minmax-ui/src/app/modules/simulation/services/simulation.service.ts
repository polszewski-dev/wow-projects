import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { SimulationStats } from '../model/SimulationStats';

@Injectable({
	providedIn: 'root'
})
export class SimulationService {
	private readonly apiUrl = environment.simulationApiUrl;

	constructor(private http: HttpClient) { }

	simulate(playerId: string) {
		return this.http.get<SimulationStats>(`${this.apiUrl}/${playerId}`);
	}
}
