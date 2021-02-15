package com.glyart.authmevelocity.proxy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Getter
public class AuthMeConfig {
    
    private List<String> authServers = Arrays.asList("auth-1", "auth-2");
    
    public static AuthMeConfig loadConfig(Path folder) throws IOException {
        File folderFile = folder.toFile();
        File file = new File(folderFile, "config.yml");
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        if (!folderFile.exists())
            folderFile.mkdir();
        
        if (!file.exists()) {
            file.createNewFile();
            
            AuthMeConfig config = new AuthMeConfig();
            mapper.writeValue(file, config);
            return config;
        }
        
        return mapper.readValue(file, AuthMeConfig.class);
    }
    
}
