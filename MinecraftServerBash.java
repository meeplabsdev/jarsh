import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;

public class MinecraftServerBash {
    public static void main(String[] args) {
        try {
            Path tailscaleDir = Paths.get("tailscale");
            Path tailscaleTgz = Paths.get("tailscale.tgz");

            if (!Files.exists(tailscaleDir)) {
                if (!Files.exists(tailscaleTgz)) {
                    System.out.println("Downloading Tailscale...");
                    URL url = new URL("https://pkgs.tailscale.com/stable/tailscale_1.80.2_amd64.tgz");
                    try (InputStream in = url.openStream()) {
                        Files.copy(in, tailscaleTgz, StandardCopyOption.REPLACE_EXISTING);
                    }
                }

                Files.createDirectories(tailscaleDir);
                
                ProcessBuilder pb = new ProcessBuilder("tar", "xvzf", tailscaleTgz.toString(), "-C", tailscaleDir.toString(), "--strip-components=1");
                pb.inheritIO();
                pb.start().waitFor();
            }

            setExecutablePermissions(Paths.get("./tailscale/tailscale"));
            setExecutablePermissions(Paths.get("./tailscale/tailscaled"));

            Files.deleteIfExists(Paths.get("tail.sock"));

            ProcessBuilder tailscaledPb = new ProcessBuilder("./tailscale/tailscaled", "--tun=userspace-networking", "--socket=./tail.sock");
            tailscaledPb.inheritIO();
            Process tailscaledProcess = tailscaledPb.start();

            ProcessBuilder tailscalePb = new ProcessBuilder("./tailscale/tailscale", "--socket=./tail.sock", "up", "--advertise-exit-node", "--advertise-routes=0.0.0.0/24", "--hostname=jarsh-exitnode", "--ssh", "--qr");
            tailscalePb.inheritIO();
            tailscalePb.start().waitFor();

            while (true) {
                try {
                    Thread.sleep(60000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void setExecutablePermissions(Path path) throws IOException {
        try {
            Files.setPosixFilePermissions(path, PosixFilePermissions.fromString("rwxr-xr-x"));
        } catch (UnsupportedOperationException e) {
            System.err.println("POSIX file permissions not supported. Attempting chmod +x via process.");
            try {
                new ProcessBuilder("chmod", "+x", path.toString()).start().waitFor();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
