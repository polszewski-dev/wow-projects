package wow.minmax.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.minmax.service.SpellService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * User: POlszewski
 * Date: 2022-11-19
 */
@WebMvcTest(BuffController.class)
class BuffControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	SpellService spellService;

	@Test
	void getAvailableBuffs() throws Exception {
		mockMvc.perform(get("/api/v1/buff/list"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(buff.getId())))
				.andExpect(jsonPath("$[0].name", is(buff.getName())))
		;

		verify(spellService).getAvailableBuffs();
	}

	@BeforeEach
	void setup() {
		createMockObjects();

		when(spellService.getAvailableBuffs()).thenReturn(List.of(buff));
	}
}