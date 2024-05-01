
package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.UCSBRecommendationRequest;
import edu.ucsb.cs156.example.repositories.UCSBRecommendationRequestRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = UCSBRecommendationRequestController.class)
@Import(TestConfig.class)
public class UCSBRecommendationRequestControllerTests extends ControllerTestCase {

        @MockBean
        UCSBRecommendationRequestRepository ucsbRecommendationRequestRepository;

        @MockBean
        UserRepository userRepository;
        // Tests for GET /api/ucsbrecommendationrequest/all
        
        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbrecommendationrequest/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/ucsbrecommendationrequest/all"))
                                .andExpect(status().is(200)); // logged
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_ucsbrecommendationrequest() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                UCSBRecommendationRequest ucsbRecommendationRequest1 = UCSBRecommendationRequest.builder()
                                .requesterEmail("adilahmed")
                                .professorEmail("pconrad")
                                .explanation("please")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();


                UCSBRecommendationRequest ucsbRecommendationRequest2 = UCSBRecommendationRequest.builder()
                                .requesterEmail("adilahmed2")
                                .professorEmail("pconrad")
                                .explanation("please")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                ArrayList<UCSBRecommendationRequest> expectedRequests = new ArrayList<>();
                expectedRequests.addAll(Arrays.asList(ucsbRecommendationRequest1, ucsbRecommendationRequest2));

                when(ucsbRecommendationRequestRepository.findAll()).thenReturn(expectedRequests);

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbrecommendationrequest/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbRecommendationRequestRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedRequests);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for POST /api/ucsbrecommendationrequest/post...

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbrecommendationrequest/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/ucsbrecommendationrequest/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_ucsbrecommendationrequest() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                UCSBRecommendationRequest ucsbRecommendationRequest1 = UCSBRecommendationRequest.builder()
                                .requesterEmail("adilahmed")
                                .professorEmail("pconrad")
                                .explanation("please")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                when(ucsbRecommendationRequestRepository.save(eq(ucsbRecommendationRequest1))).thenReturn(ucsbRecommendationRequest1);

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/ucsbrecommendationrequest/post?requesterEmail=adilahmed&professorEmail=pconrad&explanation=please&dateRequested=2022-01-03T00:00:00&dateNeeded=2022-03-11T00:00:00&done=false")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbRecommendationRequestRepository, times(1)).save(ucsbRecommendationRequest1);
                String expectedJson = mapper.writeValueAsString(ucsbRecommendationRequest1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        // Tests for GET /api/ucsbrecommendationrequest?id=...

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/ucsbrecommendationrequest?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange
                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                UCSBRecommendationRequest ucsbRecommendationRequest1 = UCSBRecommendationRequest.builder()
                                .requesterEmail("adilahmed")
                                .professorEmail("pconrad")
                                .explanation("please")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                when(ucsbRecommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.of(ucsbRecommendationRequest1));

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbrecommendationrequest?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(ucsbRecommendationRequestRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(ucsbRecommendationRequest1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(ucsbRecommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/ucsbrecommendationrequest?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(ucsbRecommendationRequestRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("UCSBRecommendationRequest with id 7 not found", json.get("message"));
        }


        // Tests for DELETE /api/ucsbrecommendationrequest?id=... 

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_recommendationrequest() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                UCSBRecommendationRequest ucsbRecommendationRequest1 = UCSBRecommendationRequest.builder()
                                .requesterEmail("adilahmed")
                                .professorEmail("pconrad")
                                .explanation("please")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                when(ucsbRecommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.of(ucsbRecommendationRequest1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsbrecommendationrequest?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbRecommendationRequestRepository, times(1)).findById(15L);
                verify(ucsbRecommendationRequestRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBRecommendationRequest with id 15 deleted", json.get("message"));
        }
        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_ucsbrecommendationrequest_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(ucsbRecommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/ucsbrecommendationrequest?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbRecommendationRequestRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBRecommendationRequest with id 15 not found", json.get("message"));
        }

        // Tests for PUT /api/ucsbrecommendationrequest?id=... 

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_ucsbrecommendationrequest() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                UCSBRecommendationRequest ucsbRecommendationRequestOrig = UCSBRecommendationRequest.builder()
                                .requesterEmail("adilahmed")
                                .professorEmail("pconrad")
                                .explanation("please")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();
                
                LocalDateTime ldt3 = LocalDateTime.parse("2022-01-03T00:00:01");

                LocalDateTime ldt4 = LocalDateTime.parse("2022-03-11T00:00:01");

                UCSBRecommendationRequest ucsbRecommendationRequestEdited = UCSBRecommendationRequest.builder()
                                .requesterEmail("adilahmed2")
                                .professorEmail("pconrad1")
                                .explanation("different")
                                .dateRequested(ldt3)
                                .dateNeeded(ldt4)
                                .done(true)
                                .build();

                String requestBody = mapper.writeValueAsString(ucsbRecommendationRequestEdited);

                when(ucsbRecommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.of(ucsbRecommendationRequestOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsbrecommendationrequest?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(ucsbRecommendationRequestRepository, times(1)).findById(67L);
                verify(ucsbRecommendationRequestRepository, times(1)).save(ucsbRecommendationRequestEdited); // should be saved with correct user
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        
        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_ucsbrecommendationrequest_that_does_not_exist() throws Exception {
                // arrange

                LocalDateTime ldt1 = LocalDateTime.parse("2022-01-03T00:00:00");

                LocalDateTime ldt2 = LocalDateTime.parse("2022-03-11T00:00:00");

                UCSBRecommendationRequest ucsbRecommendationRequestEdited = UCSBRecommendationRequest.builder()
                                .requesterEmail("adilahmed")
                                .professorEmail("pconrad")
                                .explanation("please")
                                .dateRequested(ldt1)
                                .dateNeeded(ldt2)
                                .done(false)
                                .build();

                String requestBody = mapper.writeValueAsString(ucsbRecommendationRequestEdited);

                when(ucsbRecommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/ucsbrecommendationrequest?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(ucsbRecommendationRequestRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("UCSBRecommendationRequest with id 67 not found", json.get("message"));

        }


}

