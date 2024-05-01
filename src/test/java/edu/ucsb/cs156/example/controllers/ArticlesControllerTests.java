package edu.ucsb.cs156.example.controllers;

import edu.ucsb.cs156.example.repositories.UserRepository;
import edu.ucsb.cs156.example.testconfig.TestConfig;
import edu.ucsb.cs156.example.ControllerTestCase;
import edu.ucsb.cs156.example.entities.Articles;
import edu.ucsb.cs156.example.repositories.ArticlesRepository;

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

@WebMvcTest(controllers = ArticlesController.class)
@Import(TestConfig.class)

public class ArticlesControllerTests extends ControllerTestCase{
    @MockBean
    ArticlesRepository articlesRepository;

    @MockBean
    UserRepository userRepository;

    // Tests for GET /api/ucsbarticles/all
    
    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsbarticles/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/ucsbarticles/all"))
                            .andExpect(status().is(200)); // logged
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_ucsbarticles() throws Exception {

            // arrange
            LocalDateTime ldt1 = LocalDateTime.parse("2022-04-20T00:00:00");

            Articles ucsbArticle1 = Articles.builder()
                            .title("Using testing-playground with React Testing Library")
                            .url("https://dev.to/katieraby/using-testing-playground-with-react-testing-library-26j7")
                            .explanation("Helpful when we get to front end development")
                            .email("phtcon@ucsb.edu")
                            .dateAdded(ldt1)
                            .build();

            LocalDateTime ldt2 = LocalDateTime.parse("2022-04-19T00:00:00");

            Articles ucsbArticle2 = Articles.builder()
                            .title("Handy Spring Utility Classes")
                            .url("https://twitter.com/maciejwalkowiak/status/1511736828369719300?t=gGXpmBH4y4eY9OBSUInZEg&s=09")
                            .explanation("A lot of really useful classes are built into Spring")
                            .email("phtcon@ucsb.edu")
                            .dateAdded(ldt2)
                            .build();

            ArrayList<Articles> expectedArticles = new ArrayList<>();
            expectedArticles.addAll(Arrays.asList(ucsbArticle1, ucsbArticle2));

            when(articlesRepository.findAll()).thenReturn(expectedArticles);

            // act
            MvcResult response = mockMvc.perform(get("/api/ucsbarticles/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(articlesRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedArticles);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    // Tests for POST /api/ucsbdates/post...

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsbarticles/post"))
                            .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/ucsbarticles/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_ucsbarticles() throws Exception {
            // arrange
            // arrange
            LocalDateTime ldt1 = LocalDateTime.parse("2022-04-20T00:00:00");

            Articles ucsbArticle1 = Articles.builder()
                            .title("Using testing-playground with React Testing Library")
                            .url("https://dev.to/katieraby/using-testing-playground-with-react-testing-library-26j7")
                            .explanation("Helpful when we get to front end development")
                            .email("phtcon@ucsb.edu")
                            .dateAdded(ldt1)
                            .build();

            when(articlesRepository.save(eq(ucsbArticle1))).thenReturn(ucsbArticle1);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/ucsbarticles/post?title=Using testing-playground with React Testing Library&ucrl=https://dev.to/katieraby/using-testing-playground-with-react-testing-library-26j7&explanation=Helpful when we get to front end development&email=phtcon@ucsb.edudateAdded=2022-04-20T00:00:00")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(articlesRepository, times(1)).save(ucsbArticle1);
            String expectedJson = mapper.writeValueAsString(ucsbArticle1);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }


}
