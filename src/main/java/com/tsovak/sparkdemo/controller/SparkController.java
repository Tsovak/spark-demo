package com.tsovak.sparkdemo.controller;

import com.tsovak.sparkdemo.annotation.SparkRequestMapping;
import com.tsovak.sparkdemo.model.ResponseTemplate;
import com.tsovak.sparkdemo.model.Statistics;
import com.tsovak.sparkdemo.model.User;
import com.tsovak.sparkdemo.repositories.StatisticsRepository;
import com.tsovak.sparkdemo.service.UserService;
import com.tsovak.sparkdemo.transformer.TextTransformer;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import spark.Response;
import spark.Route;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.UUID;

@Component
public class SparkController {

    @Autowired
    UserService userService;

    @Autowired
    StatisticsRepository statisticsRepository;

    @SparkRequestMapping(path = "/text", method = RequestMethod.GET, transformer = TextTransformer.class)
    @ApiOperation(value = "The text example", response = String.class)
    private Route textTransformer() {
        return (request, response) -> {
            enableCORS(response);
            return "This is some text from TextTransformer. Content-type: text/html";
        };
    }

    @SparkRequestMapping(path = "/users", method = RequestMethod.GET)
    @ApiOperation(value = "All Users", response = User.class, responseContainer = "List")
    private Route findUsers() {
        return (request, response) -> {
            enableCORS(response);
            return userService.findAll();
        };
    }

    @SparkRequestMapping(path = "/users", method = RequestMethod.POST)
    @ApiOperation(value = "Add new User", response = UUID.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "User's name", required = true, dataType = "string", paramType = "query")
    })
    private Route addUser() {
        return (request, response) -> {
            enableCORS(response);
            String name = request.queryMap("name").value();
            UUID uuid = userService.save(name);
            response.status(HttpStatus.OK.value());
            return uuid;
        };
    }

    @SparkRequestMapping(path = "/users/:uuid", method = RequestMethod.GET)
    @ApiOperation(value = "Find the User", response = User.class)
    private Route findUser() {
        return (request, response) -> {
            enableCORS(response);
            UUID uuid = UUID.fromString(request.params(":uuid"));
            Optional<User> gamer = userService.find(uuid);
            if (!gamer.isPresent()) {
                response.status(404);
                return new ResponseTemplate("No gamer with uuid '%s' found", uuid.toString());
            }
            return gamer.get();
        };
    }

    @SparkRequestMapping(path = "/users/:uuid", method = RequestMethod.DELETE)
    @ApiOperation(value = "Delete the specific User")
    private Route deleteUser() {
        return (request, response) -> {
            enableCORS(response);
            UUID uuid = UUID.fromString(request.params(":uuid"));
            userService.deleteById(uuid);
            response.status(HttpStatus.NO_CONTENT.value());
            return Optional.empty();
        };
    }

    @SparkRequestMapping(path = "/users/:uuid/activity/:activity", method = RequestMethod.POST)
    @ApiOperation(value = "Add activity")
    private Route addActivity() {
        return (request, response) -> {
            enableCORS(response);
            UUID uuid = UUID.fromString(request.params(":uuid"));
            long activity = Long.parseLong(request.params(":activity"));
            Optional<User> gamer = userService.find(uuid);
            if (!gamer.isPresent()) {
                response.status(404);
                return new ResponseTemplate("No user with uuid '%s' found", uuid.toString());
            }
            User user = gamer.get();
            Statistics statistics = new Statistics(UUID.randomUUID(), user.getUuid(), activity, LocalDateTime.now());
            user.getStatistics().add(statistics);
            userService.save(user);
            response.status(HttpStatus.OK.value());
            return Optional.empty();
        };
    }

    @SparkRequestMapping(path = "/users/:uuid/activity", method = RequestMethod.GET)
    @ApiOperation(value = "Get activity", response = Statistics.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromDate", value = "User activity date from", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "fromTo", value = "User activity date to", required = false, dataType = "string", paramType = "query")
    })
    private Route getActivity() {
        return (request, response) -> {
            enableCORS(response);
            UUID uuid = UUID.fromString(request.params(":uuid"));
            String fromDate = request.queryParams("fromDate");
            String toDate = request.queryParams("toDate");

            Optional<User> gamer = userService.find(uuid);
            if (!gamer.isPresent()) {
                response.status(404);
                return new ResponseTemplate("No user with uuid '%s' found", uuid.toString());
            }

            LocalDateTime from = LocalDateTime.now().minusYears(2000);
            LocalDateTime to = LocalDateTime.now();
            try {

                if (fromDate != null) {
                    from = LocalDateTime.parse(fromDate);
                }
                if (toDate != null) {
                    to = LocalDateTime.parse(toDate);
                }
            } catch (DateTimeParseException e) {
                response.status(400);
                return new ResponseTemplate("Cannot parse the date params. Should to use yyyy-MM-dd'T'HH:mm:ss. " + e.getLocalizedMessage());
            }

            response.status(HttpStatus.OK.value());
            return statisticsRepository.findAllByUserUuidAndDateBetweenOrderByDateDesc(uuid, from, to);
        };
    }

    @SparkRequestMapping(path = "/newest", method = RequestMethod.GET)
    @ApiOperation(value = "Find newest users per period", response = User.class)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "fromDate", value = "User registration date from", required = false, dataType = "string", paramType = "query"),
            @ApiImplicitParam(name = "fromTo", value = "User registration date to", required = false, dataType = "string", paramType = "query")
    })
    private Route findNewUsers() {
        return (request, response) -> {
            enableCORS(response);
            String fromDate = request.queryParams("fromDate");
            String toDate = request.queryParams("toDate");

            LocalDateTime from = LocalDateTime.now().minusYears(2000);
            LocalDateTime to = LocalDateTime.now();
            try {

                if (fromDate != null) {
                    from = LocalDateTime.parse(fromDate);
                }
                if (toDate != null) {
                    to = LocalDateTime.parse(toDate);
                }
            } catch (DateTimeParseException e) {
                response.status(400);
                return new ResponseTemplate("Cannot parse the date params. Should to use yyyy-MM-dd'T'HH:mm:ss. " + e.getLocalizedMessage());
            }

            return userService.findAllByRegistrationDateBetween(from, to);
        };
    }

    @SparkRequestMapping(path = "/*", method = RequestMethod.OPTIONS)
    private Route enableCORS() {
        return (request, response) -> {
            enableCORS(response);
            return "OK";
        };
    }

    private void enableCORS(Response response) {
        response.header("Access-Control-Allow-Origin", "*");
        response.header("Access-Control-Allow-Headers", "User-Agent,Keep-Alive,Content-Type");
        response.header("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.header("Access-Control-Expose-Headers", "Content-Length,Content-Range");
    }
}
