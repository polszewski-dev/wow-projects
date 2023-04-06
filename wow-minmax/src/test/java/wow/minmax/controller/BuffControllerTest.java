package wow.minmax.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import wow.commons.model.buffs.Buff;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static wow.character.model.character.BuffListType.CHARACTER_BUFF;

/**
 * User: POlszewski
 * Date: 2023-01-02
 */
@WebMvcTest(BuffController.class)
class BuffControllerTest extends ControllerTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	void getBuffs() throws Exception {
		mockMvc.perform(get("/api/v1/buff/{characterId}/{buffListType}/list", CHARACTER_KEY, CHARACTER_BUFF))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}

	@Test
	void enableBuff() throws Exception {
		Buff buff = getBuff("Fel Armor");

		mockMvc.perform(get("/api/v1/buff/{characterId}/{buffListType}/enable/{buffId}/{enabled}", CHARACTER_KEY, CHARACTER_BUFF, buff.getId(), true))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		;
	}
}