import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EncryptorDecryptorUtility {
    private byte[] data = new byte[8];
    private byte[] result = new byte[8];
    private String fileFrom;
    private String password;
    private String fileTo;

    public EncryptorDecryptorUtility(String fileFrom, String password, String fileTo) {
        this.fileFrom = fileFrom;
        this.password = password;
        this.fileTo = fileTo;
    }

    void encrypt() throws IOException {
        FileInputStream fileIn = new FileInputStream(fileFrom);
        FileOutputStream fileOut = new FileOutputStream(fileTo);

        long startTime = System.currentTimeMillis();

        byte[] passwordByte = password.getBytes();
        byte[] buffer = new byte[fileIn.available()];
        List<Byte> listBuffer = new ArrayList<>();

        while (fileIn.available() > 0) {
            fileIn.read(data, 0, 8);
            ProcessData(passwordByte, data, result);
            for (byte b : result) {
                listBuffer.add(b);
            }
        }

        for (int i = 0; i < buffer.length; i++) {
            buffer[i] = listBuffer.get(i);
        }

        fileOut.write(buffer, 0, buffer.length);

        System.out.println(System.currentTimeMillis() - startTime);

        fileIn.close();
        fileOut.close();
    }

    // этот метод шифрует файл
    private void ProcessData(final byte[] password, final byte[] data, byte[] result) {
        final int ARG_LEN = 8;
        if ((data.length != ARG_LEN) || (password.length != ARG_LEN) || (result.length != ARG_LEN)) {
            throw new RuntimeException("Wrong len");
        }

        for (int i = 0; i < 8; ++i) {
            result[i] = (byte) (data[i] ^ password[i]);
        }

    }
}
