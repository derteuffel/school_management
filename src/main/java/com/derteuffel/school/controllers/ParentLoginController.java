package com.derteuffel.school.controllers;

import com.derteuffel.school.repositories.EcoleRepository;
import com.derteuffel.school.services.CompteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by user on 23/03/2020.
 */
@Controller
@RequestMapping("/parent")
public class ParentLoginController {

    @Autowired
    private EcoleRepository ecoleRepository;

    @Autowired
    private CompteService compteService;
    @Value("${file.upload-dir}")
    private  String fileStorage;

    @GetMapping("/login")
    public String director(){
        return "parent/login";
    }

}
