package wow.minmax.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import wow.minmax.service.SimulatorService;

/**
 * User: POlszewski
 * Date: 2024-11-09
 */
@Service
public class SimulatorServiceImpl implements SimulatorService {
	private final WebClient webClient;

	public SimulatorServiceImpl(@Qualifier("simulator") WebClient webClient) {
		this.webClient = webClient;
	}

	@Override
	public String hello() {
		String result = webClient.get().retrieve().bodyToMono(String.class).block();
		return "Result is " + result;
	}
}
