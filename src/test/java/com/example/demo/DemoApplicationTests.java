package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DemoApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
    }

    @Test
    void testDate() throws Exception{
        var currDate = new java.util.Date();
        System.out.println("Start Date: " + currDate.toString() + " " + currDate.getTime());
        mockMvc.perform(
                MockMvcRequestBuilders.get("/date").accept(MediaType.ALL))
                .andExpect(status().isOk())

                .andExpect(result -> {
                    var responseDate = new java.util.Date(result.getResponse().getContentAsString());
                    System.out.println("Response Date: " + responseDate + " " + responseDate.getTime());
                    assert responseDate.getTime() > currDate.getTime()-1000;
                });

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/date").accept(MediaType.ALL))
                .andExpect(status().isBadRequest());

    }

}