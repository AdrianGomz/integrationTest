package com.socialmeli.be_java_hisp_w20_g8.services.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.socialmeli.be_java_hisp_w20_g8.dto.PostRequestDTO;
import com.socialmeli.be_java_hisp_w20_g8.dto.ProductDTO;
import com.socialmeli.be_java_hisp_w20_g8.dto.ResponseDTO;
import com.socialmeli.be_java_hisp_w20_g8.dto.SellerDTO;
import com.socialmeli.be_java_hisp_w20_g8.models.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SocialMeliIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void followTestUserAndSellerExist() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/users/1/follow/5")
                .contentType(MediaType.APPLICATION_JSON))
        .andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void followTestUserDosentExist() throws  Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.post("/users/users/7/follow/5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest());
    }

    @Test
    public void userFollowersCountTestExistingUser() throws  Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/followers/count",6)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_id").value(6))
                .andExpect(MockMvcResultMatchers.jsonPath("$.user_name").value("seller4"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.followers_count").value(1));
    }
    @Test
    public void userFollowersCountTestNotExistingUser() throws  Exception{
        this.mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/followers/count",1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Not found exception"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Doesn't exist id"));
    }

    @Test
    public void listOfSellersFollowedByUser() throws Exception{
        SellerDTO sellerDTO1 = new SellerDTO(5, "seller3");
        SellerDTO sellerDTO2 = new SellerDTO(6, "seller4");
        SellerDTO sellerDTO3 = new SellerDTO(9, "zxc");

    }

    @Test
    public void postProductTest() throws Exception{
        PostRequestDTO payloadDTO = new PostRequestDTO();
        ProductDTO productDTO = new ProductDTO(10,"silla","gamer","racer","red","special edition");
        payloadDTO.setUser_id(6);
        payloadDTO.setDate(LocalDate.now());
        payloadDTO.setCategory(1);
        payloadDTO.setProductDTO(productDTO);
        payloadDTO.setPrice(1500.0);

        ResponseDTO responseDTO = new ResponseDTO(true,"Post added successfully");

        ObjectWriter writer = new ObjectMapper().registerModule( new JavaTimeModule())
                .configure(SerializationFeature.WRAP_ROOT_VALUE, false).writer();
        String payloadJson = writer.writeValueAsString(payloadDTO);
        String responseJson = writer.writeValueAsString(responseDTO);

        MvcResult result = this.mockMvc.perform(MockMvcRequestBuilders.post("/products/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadJson))
                .andDo(print()).andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ok").value(true))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Post added successfully"))
                .andReturn();
        Assertions.assertEquals(responseJson,result.getResponse().getContentAsString());

    }

}
