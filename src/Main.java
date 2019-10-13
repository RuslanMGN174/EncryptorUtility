import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        EncryptorDecryptorUtility utility = new EncryptorDecryptorUtility();

        if (args.length != 4) {
            utility.description();
        } else {
            if (args[3].length() != 8) throw new RuntimeException("Wrong password length");
            switch (args[2]) {
                case "encrypt":
                    utility.encrypt(args[0], args[1], args[3]);
                    break;
                case "decrypt":
                    utility.encrypt(args[0], args[1], args[3]);
                    break;
                default:
                    throw new RuntimeException("argument must be encrypt or decrypt");
            }
        }
    }
}
