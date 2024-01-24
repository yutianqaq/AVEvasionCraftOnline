package com.yutian4060.avevasioncraftonline.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "bypassav")
public class BypassAVConfigProperties {

    @JsonIgnore
    private String templatesDirectory;
    @JsonIgnore
    private String storageDirectory;
    @JsonIgnore
    private String compilerWorkDirectory;

    private Map<String, Map<String, List<String>>> templatesMapping;
    @JsonIgnore
    private String compilerC;
    @JsonIgnore
    private String compilerNim;
    @JsonIgnore
    private String compilerGolang;


}
