package com.example.web;

import com.example.dao.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    @Autowired
    UserRepository userRepository;

    @GetMapping(value = "/users")
    public String lisusers(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "listUsers";
    }
}
