import java.io.*;

public class EncryptorDecryptorUtility {

    private static int bufferSize = 64_000;
    private static byte[] data = new byte[8];
    private static byte[] result = new byte[8];
    private static byte[] writeBuffer = new byte[bufferSize];
    private static byte[] passwordByte;

    public static void main(String[] args) throws IOException {

        if (args.length != 4) {
            description();
        } else {
            if (args[3].length() != 8) throw new RuntimeException("Wrong password length");
            switch (args[2]) {
                case "encrypt":
                    encrypt(args[0], args[1], args[3], true);
                    break;
                case "decrypt":
                    encrypt(args[0], args[1], args[3], false);
                    break;
                default:
                    throw new RuntimeException("argument must be encrypt or decrypt");
            }
        }
    }

    private static void encrypt(String fileFrom, String fileTo, String password, boolean encrypt) throws IOException {

        //Потоки чтения и записи
        InputStream fileIn = new BufferedInputStream(new FileInputStream(fileFrom));
        BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(fileTo));

        //Получаем байты пароля
        passwordByte = password.getBytes();

        //буферы чтения и записи
        byte[] buffer = new byte[bufferSize];

        //Поток чтения из буфера
        InputStream fromBufferReader = new ByteArrayInputStream(buffer);


        //Получаем размер файла
        String fileSize = String.valueOf(fileIn.available());
        byte[] fileSizeBuffer = new byte[32];

        //Во время шифрования всталяем инфо о рзмере файла в файл
        //кодируем его
        if (encrypt) {
            fromBufferReader = new ByteArrayInputStream(fileSizeBuffer);
            System.arraycopy(fileSize.getBytes(), 0, fileSizeBuffer, 0, fileSize.length());
            fileOut.write(fillWriteBuffer(fromBufferReader), 0, fileSizeBuffer.length);
            fileOut.flush();
        }

        //В овремя раскодировки забираем из файла инфо о его размере
        if (!encrypt) {
            int countFileSizeBytes = fileIn.read(fileSizeBuffer);
            fromBufferReader = new ByteArrayInputStream(fileSizeBuffer);
            fileSize = new String(fillWriteBuffer(fromBufferReader)).trim();
        }


        //читаем данные из файла в буфер для чтения
        while (fileIn.available() > 0) {
            int countBytes = fileIn.read(buffer);

            fromBufferReader = new ByteArrayInputStream(buffer);

            //запись файла данными из буфера
            if (encrypt) {
                if (countBytes < bufferSize){
                    countBytes = countBytes / passwordByte.length * passwordByte.length + passwordByte.length;
                }

                fileOut.write(fillWriteBuffer(fromBufferReader), 0, countBytes);
                fileOut.flush();
            }

            if (!encrypt) {
                if (countBytes < bufferSize){
                    countBytes = Integer.parseInt(fileSize) % bufferSize;
                }
                fileOut.write(fillWriteBuffer(fromBufferReader), 0, countBytes);
                fileOut.flush();
            }

        }

        fileIn.close();
        fileOut.close();
    }

    private static byte[] fillWriteBuffer(InputStream fromBufferReader) {
        int index = 0;
        try {
            while (fromBufferReader.available() > 0) {
                fromBufferReader.read(data, 0, 8);
                ProcessData(passwordByte, data, result);
                System.arraycopy(result, 0, writeBuffer, index, result.length);
                index += result.length;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return writeBuffer;
    }

    // этот метод шифрует файл
    private static void ProcessData(final byte[] password, final byte[] data, byte[] result) {
        final int ARG_LEN = 8;
        if ((data.length != ARG_LEN) || (password.length != ARG_LEN) || (result.length != ARG_LEN)) {
            throw new RuntimeException("Wrong len");
        }

        for (int i = 0; i < 8; ++i) {
            result[i] = (byte) (data[i] ^ password[i]);
        }
    }

   private static void description() {
        System.out.println(
                "This utility can encrypt and decrypt files.\n" +
                        "\n" +
                        "For program start, you need enter 4 parameters through a space:\n" +
                        "1. Input file name including path.\n" +
                        "2. Output file name including path.\n" +
                        "3. Mode: \"encrypt\" or \"decrypt\" without quotes.\n" +
                        "4. Password (8 characters).");
    }
}