package com.yutian4060.avevasioncraftonline.controller;

import com.yutian4060.avevasioncraftonline.config.BypassAVConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BypassAVConfigController {

    @Autowired
    private BypassAVConfigProperties configProperties;

    @GetMapping("/avevasion/config")
    public BypassAVConfigProperties getConfig() {
        return configProperties;
    }

}
