package pt.ulusofona.aed.deisiworldmeter;

public class Result {
    public boolean success;
    public String error;
    public String result;

    public Result(boolean success, String error, String result) {
        this.success = success;
        this.error = error;
        this.result = result;
    }
}