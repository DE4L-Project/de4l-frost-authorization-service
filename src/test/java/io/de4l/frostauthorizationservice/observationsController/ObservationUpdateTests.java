package io.de4l.frostauthorizationservice.observationsController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ObservationUpdateTests {

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
	void performUpdateRequest_AdminAndWithPrivateThing_Ok() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITHOUT_PROPERTIES)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user")
	void performUpdateRequest_UserAndWithPrivateThing_NotFound() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITHOUT_PROPERTIES)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	void performUpdateRequest_AnonymousAndWithPrivateThing_NotFound() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITHOUT_PROPERTIES)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isUnauthorized());
	}

	// PUBLIC OBSERVATIONS

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performUpdateRequest_AdminAndWithPublicProperty_Ok() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITH_DE4L_PUBLIC)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user")
	void performUpdateRequest_UserAndWithPublicProperty_NotFound() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITH_DE4L_PUBLIC)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	void performUpdateRequest_AnonymousAndWithPublicProperty_Notfound() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITH_DE4L_PUBLIC)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isUnauthorized());
	}

	// OBSERVATIONS WITH OWNER

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performUpdateRequest_AdminAndWithThingOwnerProperty_Ok() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITH_DE4L_OWNER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user")
	void performUpdateRequest_ThingOwnerAndWithThingOwnerProperty_Ok() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITH_DE4L_OWNER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	void performUpdateRequest_AnonymousAndWithThingOwnerProperty_NotFound() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITH_DE4L_OWNER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isUnauthorized());
	}

	// OBSERVATIONS WITH CONSUMER

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performUpdateRequest_AdminAndWithThingConsumerProperty_Ok() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITH_DE4L_CONSUMER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user")
	void performUpdateRequest_ThingConsumerAndWithThingConsumerProperty_NotFound() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITH_DE4L_CONSUMER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	void performUpdateRequest_AnonymousAndWithThingConsumerProperty_NotFound() throws Exception {
		mockMvc.perform(patch(OBSERVATION_WITH_DE4L_CONSUMER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isUnauthorized());
	}

}
