package io.de4l.frostauthorizationservice.thingsController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ThingReadTests {

	@Autowired
	private MockMvc mockMvc;

	private final String PATH_FROST = "/FROST-Server/v1.0/";
	private final String THING_WITHOUT_PROPERTIES = PATH_FROST + "Things(1)";
	// with property de4lPublic : true
	private final String THING_WITH_DE4L_PUBLIC = PATH_FROST + "Things(2)";
	// with property de4lOwner : "user"
	private final String THING_WITH_DE4L_OWNER = PATH_FROST + "Things(3)";
	// with property de4lConsumer : ["user3, user, user2"]
	private final String THING_WITH_DE4L_CONSUMER = PATH_FROST + "Things(4)";

	// THINGS WITHOUT PROPERTIES

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performReadRequest_AdminAndWithPrivateThing_Ok() throws Exception {
		mockMvc.perform(get(THING_WITHOUT_PROPERTIES))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name", is("UBA: DERP025 - Wörth-Marktplatz")));
	}

	@Test
	@WithMockUser(username = "user")
	void performReadRequest_UserAndWithPrivateThing_Forbidden() throws Exception {
		mockMvc.perform(get(THING_WITHOUT_PROPERTIES))
				.andExpect(status().isNotFound());
	}

	@Test
	void performReadRequest_AnonymousAndWithPrivateThing_NotFound() throws Exception {
		mockMvc.perform(get(THING_WITHOUT_PROPERTIES))
				.andExpect(status().isNotFound());
	}

	// PUBLIC THINGS

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performReadRequest_AdminAndWithPublicProperty_Ok() throws Exception {
		mockMvc.perform(get(THING_WITH_DE4L_PUBLIC))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name", is("UBA: DERP024 - Koblenz-Friedrich-Ebert-Ring")));
	}

	@Test
	@WithMockUser(username = "user")
	void performReadRequest_UserAndWithPublicProperty_Ok() throws Exception {
		mockMvc.perform(get(THING_WITH_DE4L_PUBLIC))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name", is("UBA: DERP024 - Koblenz-Friedrich-Ebert-Ring")));
	}

	@Test
	void performReadRequest_AnonymousAndWithPublicProperty_Ok() throws Exception {
		mockMvc.perform(get(THING_WITH_DE4L_PUBLIC))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name", is("UBA: DERP024 - Koblenz-Friedrich-Ebert-Ring")));
	}

	// THINGS WITH OWNER

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performReadRequest_AdminAndWithThingOwnerProperty_Ok() throws Exception {
		mockMvc.perform(get(THING_WITH_DE4L_OWNER))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name", is("UBA: DERP023 - Worms-Hagenstraße")));
	}

	@Test
	@WithMockUser(username = "user")
	void performReadRequest_ThingOwnerAndWithThingOwnerProperty_Ok() throws Exception {
		mockMvc.perform(get(THING_WITH_DE4L_OWNER))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name", is("UBA: DERP023 - Worms-Hagenstraße")));
	}

	@Test
	void performReadRequest_AnonymousAndWithThingOwnerProperty_NotFound() throws Exception {
		mockMvc.perform(get(THING_WITH_DE4L_OWNER))
				.andExpect(status().isNotFound());
	}

	// THINGS WITH CONSUMER

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performReadRequest_AdminAndWithThingConsumerPropertyProperty_Ok() throws Exception {
		mockMvc.perform(get(THING_WITH_DE4L_CONSUMER))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name", is("UBA: DERP022 - Bad Kreuznach-Bosenheimer Straße")));
	}

	@Test
	@WithMockUser(username = "user")
	void performReadRequest_ThingConsumerAndWithThingConsumerPropertyProperty_Ok() throws Exception {
		mockMvc.perform(get(THING_WITH_DE4L_CONSUMER))
				.andExpect(status().isOk())
				.andExpect(jsonPath("name", is("UBA: DERP022 - Bad Kreuznach-Bosenheimer Straße")));
	}

	@Test
	void performReadRequest_AnonymousAndWithThingConsumerPropertyProperty_NotFound() throws Exception {
		mockMvc.perform(get(THING_WITH_DE4L_CONSUMER))
				.andExpect(status().isNotFound());
	}


}
