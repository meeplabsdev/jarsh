import java.io.IOException;

public class MinecraftServerBash {
    public static void main(String[] args) {
        try {
            ProcessBuilder pb = new ProcessBuilder("/bin/bash");
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}

