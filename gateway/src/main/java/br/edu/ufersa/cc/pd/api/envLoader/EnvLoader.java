package br.edu.ufersa.cc.pd.api.envLoader;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./gateway")
           .load();

    public static String getEnv(String key) {
        return dotenv.get(key);
    }
}