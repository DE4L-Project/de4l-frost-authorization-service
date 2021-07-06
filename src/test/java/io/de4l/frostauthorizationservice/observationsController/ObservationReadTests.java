package io.de4l.frostauthorizationservice.observationsController;

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
class ObservationReadTests {

	@Autowired
	private MockMvc mockMvc;

	private final String PATH_FROST = "/FROST-Server/v1.0/";
	// associated thing without properties (without de4lPublic -> private)
	private final String OBSERVATION_WITHOUT_PROPERTIES = PATH_FROST + "Observations(1)";
	// associated thing with property de4lPublic : true
	private final String OBSERVATION_WITH_DE4L_PUBLIC = PATH_FROST + "Observations(6)";
	// associated thing with property de4lOwner : "user"
	private final String OBSERVATION_WITH_DE4L_OWNER = PATH_FROST + "Observations(7)";
	// associated thing with property de4lConsumer : ["user3, user, user2"]
	private final String OBSERVATION_WITH_DE4L_CONSUMER = PATH_FROST + "Observations(8)";

	// OBSERVATIONS WITHOUT PROPERTIES

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performReadRequest_AdminAndWithPrivateThing_Ok() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITHOUT_PROPERTIES))
				.andExpect(status().isOk())
				.andExpect(jsonPath("result", is(12.6)));
	}

	@Test
	@WithMockUser(username = "user")
	void performReadRequest_UserAndWithPrivateThing_NotFound() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITHOUT_PROPERTIES))
				.andExpect(status().isNotFound());
	}

	@Test
	void performReadRequest_AnonymousAndWithPrivateThing_NotFound() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITHOUT_PROPERTIES))
				.andExpect(status().isNotFound());
	}

	// PUBLIC OBSERVATIONS

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performReadRequest_AdminAndWithPublicProperty_Ok() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITH_DE4L_PUBLIC))
				.andExpect(status().isOk())
				.andExpect(jsonPath("result", is(12.6)));
	}

	@Test
	@WithMockUser(username = "user")
	void performReadRequest_UserAndWithPublicProperty_Ok() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITH_DE4L_PUBLIC))
				.andExpect(status().isOk())
				.andExpect(jsonPath("result", is(12.6)));
	}

	@Test
	void performReadRequest_AnonymousAndWithPublicProperty_Ok() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITH_DE4L_PUBLIC))
				.andExpect(status().isOk())
				.andExpect(jsonPath("result", is(12.6)));
	}

	// OBSERVATIONS WITH OWNER

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performReadRequest_AdminAndWithThingOwnerProperty_Ok() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITH_DE4L_OWNER))
				.andExpect(status().isOk())
				.andExpect(jsonPath("result", is(12.6)));
	}

	@Test
	@WithMockUser(username = "user")
	void performReadRequest_ThingOwnerAndWithThingOwnerProperty_Ok() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITH_DE4L_OWNER))
				.andExpect(status().isOk())
				.andExpect(jsonPath("result", is(12.6)));
	}

	@Test
	void performReadRequest_AnonymousAndWithThingOwnerProperty_NotFound() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITH_DE4L_OWNER))
				.andExpect(status().isNotFound());
	}

	// OBSERVATIONS WITH CONSUMER

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performReadRequest_AdminAndWithThingConsumerPropertyProperty_Ok() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITH_DE4L_CONSUMER))
				.andExpect(status().isOk())
				.andExpect(jsonPath("result", is(12.6)));
	}

	@Test
	@WithMockUser(username = "user")
	void performReadRequest_ThingConsumerAndWithThingConsumerPropertyProperty_Ok() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITH_DE4L_CONSUMER))
				.andExpect(status().isOk())
				.andExpect(jsonPath("result", is(12.6)));
	}

	@Test
	void performReadRequest_AnonymousAndWithThingConsumerPropertyProperty_NotFound() throws Exception {
		mockMvc.perform(get(OBSERVATION_WITH_DE4L_CONSUMER))
				.andExpect(status().isNotFound());
	}


}
