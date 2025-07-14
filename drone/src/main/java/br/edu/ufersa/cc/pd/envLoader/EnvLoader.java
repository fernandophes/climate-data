package br.edu.ufersa.cc.pd.envLoader;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvLoader {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./drone")
           .load();

    public static String getEnv(String key) {
        return dotenv.get(key);
    }
}