package com.tsovak.sparkdemo.controller;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.tsovak.sparkdemo.TestConfig;
import com.tsovak.sparkdemo.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import spark.servlet.SparkApplication;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {TestConfig.class})
@TestPropertySource(value = "classpath:application-test.yml")
public class SparkControllerTest {

    public static SparkServer<SparkControllerTestSparkApplication> testServer = new SparkServer<>(SparkControllerTestSparkApplication.class, 4567);

    @Autowired
    MongoTemplate mongoTemplate;

    public static class SparkControllerTestSparkApplication implements SparkApplication {
        @Override
        public void init() {
        }
    }

    @Test
    public void findUsersTest() throws Exception {
        User user = new User(UUID.randomUUID(), "testName", LocalDateTime.now());
        User user1 = new User(UUID.randomUUID(), "testName2", LocalDateTime.now());
        mongoTemplate.save(user);
        mongoTemplate.save(user1);
        GetMethod get = testServer.get("/users", false);
        HttpResponse httpResponse = testServer.execute(get);
        assertEquals(200, httpResponse.code());
        List<User> users = fromJson(httpResponse.body());
        assertEquals(2, users.size());
    }

    @Test
    public void findSpecificUserTest() throws Exception {
        User user = new User(UUID.randomUUID(), "testName", LocalDateTime.now());
        mongoTemplate.save(user);
        GetMethod get = testServer.get("/users/" + user.getUuid().toString(), false);
        HttpResponse httpResponse = testServer.execute(get);
        assertEquals(200, httpResponse.code());
        User expectedUser = new Gson().fromJson(new String(httpResponse.body()), User.class);
        assertEquals(expectedUser, user);
    }

    @Test
    public void createUserTest() throws Exception {
        PostMethod post = testServer.post("/users?name=newName", "", false);
        HttpResponse httpResponse = testServer.execute(post);
        assertEquals(200, httpResponse.code());
        String uuid = new String(httpResponse.body()).replaceAll("\"", "");

        GetMethod get = testServer.get("/users/" + uuid, false);
        HttpResponse httpResponseGet = testServer.execute(get);
        assertEquals(200, httpResponseGet.code());
        User expectedUser = new Gson().fromJson(new String(httpResponseGet.body()), User.class);

        assertEquals(expectedUser.getName(), "newName");
    }
    
    private List<User> fromJson(byte[] bytes) {
        Type type = new TypeToken<List<User>>() {}.getType();
        List<User> users = new Gson().fromJson(new String(bytes), type);
        return users;
    }
}