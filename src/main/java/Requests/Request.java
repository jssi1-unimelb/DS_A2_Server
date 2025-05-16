package Requests;

public abstract class Request {
    public String role;
    private String type;

    public Request(String type) {
        this.type = type;
        this.role = "client";
    }
}
