package io.de4l.frostauthorizationservice.thingsController;

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
class ThingUpdateTests {

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
	void performUpdateRequest_AdminAndWithPrivateThing_Ok() throws Exception {
		mockMvc.perform(patch(THING_WITHOUT_PROPERTIES)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user")
	void performUpdateRequest_UserAndWithPrivateThing_NotFound() throws Exception {
		mockMvc.perform(patch(THING_WITHOUT_PROPERTIES)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	void performUpdateRequest_AnonymousAndWithPrivateThing_NotFound() throws Exception {
		mockMvc.perform(patch(THING_WITHOUT_PROPERTIES)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isUnauthorized());
	}

	// PUBLIC THINGS

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performUpdateRequest_AdminAndWithPublicProperty_Ok() throws Exception {
		mockMvc.perform(patch(THING_WITH_DE4L_PUBLIC)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user")
	void performUpdateRequest_UserAndWithPublicProperty_NotFound() throws Exception {
		mockMvc.perform(patch(THING_WITH_DE4L_PUBLIC)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	void performUpdateRequest_AnonymousAndWithPublicProperty_Notfound() throws Exception {
		mockMvc.perform(patch(THING_WITH_DE4L_PUBLIC)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isUnauthorized());
	}

	// THINGS WITH OWNER

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performUpdateRequest_AdminAndWithThingOwnerProperty_Ok() throws Exception {
		mockMvc.perform(patch(THING_WITH_DE4L_OWNER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user")
	void performUpdateRequest_ThingOwnerAndWithThingOwnerProperty_Ok() throws Exception {
		mockMvc.perform(patch(THING_WITH_DE4L_OWNER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	void performUpdateRequest_AnonymousAndWithThingOwnerProperty_NotFound() throws Exception {
		mockMvc.perform(patch(THING_WITH_DE4L_OWNER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isUnauthorized());
	}

	// THINGS WITH CONSUMER

	@Test
	@WithMockUser(username = "admin", authorities = {"frost_admin"})
	void performUpdateRequest_AdminAndWithThingConsumerProperty_Ok() throws Exception {
		mockMvc.perform(patch(THING_WITH_DE4L_CONSUMER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user")
	void performUpdateRequest_ThingConsumerAndWithThingConsumerProperty_NotFound() throws Exception {
		mockMvc.perform(patch(THING_WITH_DE4L_CONSUMER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isNotFound());
	}

	@Test
	void performUpdateRequest_AnonymousAndWithThingConsumerProperty_NotFound() throws Exception {
		mockMvc.perform(patch(THING_WITH_DE4L_CONSUMER)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
				.content("{}")
				.with(csrf()))
				.andExpect(status().isUnauthorized());
	}

}
