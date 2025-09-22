package co.edu.eci.microframework;

import co.edu.eci.microframework.annotations.*;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class MicroServer {

    private final int port;
    private HttpServer server;

    public MicroServer(int port) {
        this.port = port;
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
            this.server.setExecutor(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Registrar todos los métodos anotados con @GET
     */
    public void register(Object controller) {
        for (Method method : controller.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(GET.class)) {
                GET mapping = method.getAnnotation(GET.class);
                String path = mapping.value();
                registerEndpoint(path, controller, method);
            }
        }
    }

    /**
     * Registrar un endpoint en el servidor
     */
    private void registerEndpoint(String path, Object controller, Method method) {
        try {
            server.createContext(path.split("\\{")[0], exchange -> {
                if (!"GET".equals(exchange.getRequestMethod())) {
                    sendResponse(exchange, 405, "Método no permitido");
                    return;
                }

                try {
                    // Query params (?x=1&y=2)
                    Map<String, String> queryParams = getQueryParams(exchange.getRequestURI());

                    // Path params (/users/{id})
                    Map<String, String> pathParams = getPathParams(exchange.getRequestURI(), path);

                    // Construcción de argumentos
                    Parameter[] params = method.getParameters();
                    Object[] args = new Object[params.length];

                    for (int i = 0; i < params.length; i++) {
                        Parameter p = params[i];
                        if (p.isAnnotationPresent(QueryParam.class)) {
                            String qName = p.getAnnotation(QueryParam.class).value();
                            args[i] = queryParams.get(qName);
                        } else if (p.isAnnotationPresent(PathParam.class)) {
                            String pName = p.getAnnotation(PathParam.class).value();
                            args[i] = pathParams.get(pName);
                        }
                    }

                    Object result = method.invoke(controller, args);
                    sendResponse(exchange, 200, result.toString());

                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, 500, "Error interno");
                }
            });

            System.out.println("Registrado: GET " + path);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Iniciar servidor
     */
    public void start() {
        server.start();
        System.out.println("Servidor iniciado en http://localhost:" + port);
    }


    /**
     * Apagar servidor
     */
    public void shutdown() {
        if (server != null) {
            server.stop(0);
            System.out.println("Servidor detenido.");
        }
    }

    private Map<String, String> getQueryParams(URI uri) {
        Map<String, String> params = new HashMap<>();
        String query = uri.getQuery();
        if (query != null) {
            for (String pair : query.split("&")) {
                String[] kv = pair.split("=");
                if (kv.length == 2) params.put(kv[0], kv[1]);
            }
        }
        return params;
    }

    private Map<String, String> getPathParams(URI uri, String pathTemplate) {
        Map<String, String> params = new HashMap<>();
        String[] requestParts = uri.getPath().split("/");
        String[] templateParts = pathTemplate.split("/");

        if (requestParts.length == templateParts.length) {
            for (int i = 0; i < templateParts.length; i++) {
                if (templateParts[i].startsWith("{") && templateParts[i].endsWith("}")) {
                    String key = templateParts[i].substring(1, templateParts[i].length() - 1);
                    params.put(key, requestParts[i]);
                }
            }
        }
        return params;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
