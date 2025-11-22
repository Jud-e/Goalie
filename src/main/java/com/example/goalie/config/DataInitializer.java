//package com.example.goalie.config;
//
//import com.example.goalie.model.User;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DataInitializer implements CommandLineRunner {
//    private final AppService appService;
//    public DataInitializer(AppService appService) {        this.appService = appService;    }
//    @Override
//    public void run(String... args) throws Exception {        // Create premium user: uc / 1234         if (!appService.emailExists("uc@test.com")) {            User premiumUser = new User();            premiumUser.setName("uc");            premiumUser.setEmail("uc@test.com");            premiumUser.setPassword("1234");            premiumUser.setSubscription(true); - Premium user
//            premiumUser.setRole("player");            premiumUser.setSkill("advanced");            premiumUser.setTournament(null);            appService.createUser(premiumUser);            System.out.println("Created premium test user: uc / 1234");        }
//        // Create regular user: uchechi / 1234         if (!appService.emailExists("uchechi@test.com")) {            User regularUser = new User();            regularUser.setName("uchechi");            regularUser.setEmail("uchechi@test.com");            regularUser.setPassword("1234");            regularUser.setSubscription(false); - Regular user
//            regularUser.setRole("player");            regularUser.setSkill("beginner");            regularUser.setTournament(null);            appService.createUser(regularUser);            System.out.println("Created regular test user: uchechi / 1234");        }    }}
//
