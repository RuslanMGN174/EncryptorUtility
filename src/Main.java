import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String fileFrom = "C:\\Users\\Vegas\\Desktop\\new\\123.mp4";
        String fileTo = "C:\\Users\\Vegas\\Desktop\\new\\123.mp4.encrypted";
        String password = "12345678";

        EncryptorDecryptorUtility utility = new EncryptorDecryptorUtility(fileFrom, password, fileTo);
        utility.encrypt();

    }
}
