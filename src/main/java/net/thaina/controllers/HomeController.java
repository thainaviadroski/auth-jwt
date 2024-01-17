package net.thaina.controllers;

import net.thaina.domain.AuthToken;
import net.thaina.domain.Users;
import net.thaina.security.TokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {
    @GetMapping("home")
    public String getHome() {
        return "Home";
    }

    @PostMapping("login")
    public ResponseEntity<AuthToken> login(@RequestBody Users user) {
        System.out.println(user.toString());
        // service for user
        // getUserByLoginAndPassword
        // saveUser
        // Encode e decode password
        if (user.getLogin().equals("jake") && user.getPassword().equals("banana")) {
            return ResponseEntity.ok(TokenUtil.encodeToken(user));
        }
        return ResponseEntity.status(403).build();
    }

    @GetMapping("products")
    public String getProduct() {
        return "Products";
    }
}
