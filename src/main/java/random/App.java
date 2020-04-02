package random;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.google.common.io.Files;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

public class App {

    private static String ACCESS_TOKEN;

    public static void main(String[] args) throws DbxException {
        if (args.length < 1) {
            System.out.println("Provide file location");
            return;
        }
        loadToken();

        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        DbxClientV2 client = new DbxClientV2(config, ACCESS_TOKEN);

        String testFile = args[0];
        ByteArrayOutputStream os = null;
        InputStream is = null;
        try {
            String fileExtension = Files.getFileExtension(testFile);
            File input = new File(testFile);
            BufferedImage image = ImageIO.read(input);
            os = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", os);
            is = new ByteArrayInputStream(os.toByteArray());
            String timeStamp = String.format("%1$tY-%1$tm-%1$td-%1$tH%1$tM%1$tS", new Date());
            String newFileName = String.format("/%s-image.%s", timeStamp, fileExtension);
            FileMetadata metadata = client.files().uploadBuilder(newFileName).uploadAndFinish(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(os).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                Objects.requireNonNull(is).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void loadToken() {
        try (InputStream source = App.class.getResourceAsStream("/config.properties")) {
            Properties prop = new Properties();
            prop.load(source);
            ACCESS_TOKEN = prop.getProperty("ACCESS_TOKEN");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
