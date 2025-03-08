import java.io.IOException;

public class BashServer {
    public static void main(String[] args) {
        try {
            ProcessBuilder bashPb = new ProcessBuilder("/bin/bash");
            bashPb.inheritIO();
            bashPb.start().waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
