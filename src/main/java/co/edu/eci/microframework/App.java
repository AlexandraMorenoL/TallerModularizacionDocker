package co.edu.eci.microframework;

import co.edu.eci.microframework.annotations.*;

public class App {
        public static void main(String[] args) throws Exception {
            MicroServer server = new MicroServer(34000);
            server.start();                     // ← primero iniciar
            server.register(new HelloController()); // ← luego registrar controladores
        }

    private static int getPort(){
        String p = System.getenv("PORT");
        if (p != null) return Integer.parseInt(p);
        return 34000;
        }


    public static class HelloController {
        @GET("/greeting")
        public String greet(@QueryParam("name") String name){
            if (name == null || name.isBlank()) name = "World";
            return "Hellooo     , " + name + "!";
        }

        @GET("/users/{id}")
        public String userById(@PathParam("id") String id){
            return "User id = " + id;
        }
    }
}
