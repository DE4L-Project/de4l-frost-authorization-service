package io.de4l.frostauthorizationservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
//@ContextConfiguration(classes = {BaseRestController.class})
class FrostAuthorizationServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(username = "admin", authorities = { "frost_admin", "USER" })
	public void shouldReturnDefaultMessage() throws Exception {
		// when(keycloakUser.isAdmin()).thenReturn(true);
		this.mockMvc.perform(get("/FROST-Server/v1.0/Test"))
				.andDo(print())
				.andExpect(status().isOk());
				//.andExpect(content().string(containsString("hi")));
	}

}
