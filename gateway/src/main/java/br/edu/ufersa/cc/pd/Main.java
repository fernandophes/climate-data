// package br.edu.ufersa.cc.pd;

// import br.edu.ufersa.cc.pd.api.envLoader.EnvLoader;
// import br.edu.ufersa.cc.pd.api.gateway.Gateway;
// import br.edu.ufersa.cc.pd.utils.Constants;

// import java.io.IOException;
// import java.net.InetSocketAddress;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Executors;
// import java.util.concurrent.TimeUnit;

// public class Main {

//     private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

//     public static void main(final String[] args) throws InterruptedException, IOException {
//         final var gateway = launchViaEnv();
//         EXECUTOR.submit(gateway);

//         EXECUTOR.shutdown();
//         EXECUTOR.awaitTermination(1, TimeUnit.MINUTES);
//     }

//     private static Gateway launchViaEnv() {
//         final var port = Integer.parseInt(EnvLoader.getEnv("GATEWAY_PORT"));

//         final var address = new InetSocketAddress(Constants.getDefaultHost(), port);
//         final var gateway = new Gateway(address, port);

//         return gateway;
//     }

// }
