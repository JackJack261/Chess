package client;

public class Main {
    public static void main(String[] args) {
        // This is the URL of your server (make sure the port matches)
        var serverUrl = "http://localhost:8080";

        // Create a new Client and call its run loop
        new Client(serverUrl).run();
    }
}