import java.io.*;

public class EncryptorDecryptorUtility {
    private int bufferSize = 64_000;
    private byte[] data = new byte[8];
    private byte[] result = new byte[8];
    private byte[] buffer;
    
    void encrypt(String fileFrom, String fileTo, String password) throws IOException {

        //Потоки чтения и записи
        InputStream fileIn = new BufferedInputStream(new FileInputStream(fileFrom));
        FileOutputStream fileOut = new FileOutputStream(fileTo);

        //Получаем байты пароля
        byte[] passwordByte = password.getBytes();

        //буферы чтения и записи
        buffer = new byte[bufferSize];
        byte[] writeBuffer;

        //Поток чтения их буфера
        InputStream fromBufferReader = new ByteArrayInputStream(buffer);

        while (true) {
            int countBytes = fileIn.read(buffer);

            if (countBytes == -1) break;
            if (countBytes > 0) {
                buffer = new byte[countBytes];
                writeBuffer = new byte[countBytes];
                int index = 0;

                while (fromBufferReader.available() > 0) {
                    fromBufferReader.read(data, 0, 8);
                    ProcessData(passwordByte, data, result);

                    for (byte b : result) {
                        if (index < writeBuffer.length) {
                            writeBuffer[index] = b;
                            ++index;
                        }
                    }
                }
                System.out.println(writeBuffer.length);
                fileOut.write(writeBuffer, 0, writeBuffer.length);
            }
        }

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

    void description() {
        System.out.println(
                "Утилита умеет шифровать и расшифровывать файлы.\n" +
                        "\n" +
                        "В параметрах запуска программы принимаются 4 параметра:\n" +
                        "1. Имя входного файла.\n" +
                        "2. Имя выходного файла.\n" +
                        "3. Режим: шифровать или расшифровывать.\n" +
                        "4. Пароль (8 символов).");
    }
}
