import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { SimulationResponse } from '../model/SimulationResponse';

@Injectable({
	providedIn: 'root'
})
export class SimulationService {
	private readonly apiUrl = environment.simulationApiUrl;

	constructor(private http: HttpClient) { }

	simulate(characterId: string) {
		return this.http.get<SimulationResponse>(`${this.apiUrl}/${characterId}`);
	}
}
