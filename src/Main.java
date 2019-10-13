import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        String fileFrom = "E:\\new\\new.txt";
        String fileTo = "E:\\new\\new2.txt";
        String password = "12345678";

        EncryptorDecryptorUtility utility = new EncryptorDecryptorUtility(fileFrom, password, fileTo);
        utility.encrypt();

    }
}
