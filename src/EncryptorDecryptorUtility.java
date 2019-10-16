import java.io.*;
import java.util.Arrays;

public class EncryptorDecryptorUtility {
    private int bufferSize = 64_000;
    private byte[] data = new byte[8];
    private byte[] result = new byte[8];
    private byte[] passwordByte;
    private byte[] writeBuffer;

    void encrypt(String fileFrom, String fileTo, String password, boolean encrypt) throws IOException {

        //Потоки чтения и записи
        InputStream fileIn = new BufferedInputStream(new FileInputStream(fileFrom));
        BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(fileTo));

        passwordByte = password.getBytes();

        byte[] buffer = new byte[bufferSize];
        writeBuffer = new byte[bufferSize];

        //Поток чтения из буфера
        InputStream fromBufferReader = new ByteArrayInputStream(buffer);

        int countBytes = fileIn.read(buffer);

        //читаем данные из файла в буфер для чтения
        //пока количество прочитанныйх байт идентично размеру буфера
        while (countBytes == buffer.length) {
            readFromBuffer(fromBufferReader);
            fileOut.write(writeBuffer, 0, bufferSize);
            fileOut.flush();
            countBytes = fileIn.read(buffer);
        }

        //читаем остатки или
        //если количество прочитанныйх байт меньше буфера
        if (countBytes < buffer.length) {

            int fileSizeExtra;
            if (countBytes % password.length() != 0) {
                fileSizeExtra = countBytes / passwordByte.length * passwordByte.length + passwordByte.length;
            } else {
                fileSizeExtra = countBytes / passwordByte.length * passwordByte.length;
            }

            byte[] recidue = new byte[fileSizeExtra];
            System.arraycopy(buffer, 0, recidue, 0, recidue.length);
            readFromBuffer(new ByteArrayInputStream(recidue));
            System.arraycopy(writeBuffer, 0, recidue, 0, recidue.length);

            //пишем в файл при расшифровке
            if (!encrypt) {
                int lastBlockSize = recidue.length - 2;
                fileOut.write(recidue, 0, lastBlockSize);
                fileOut.flush();

            }

            //пишем в файл при зашифровке
            if (encrypt) {
                fileOut.write(recidue, 0, recidue.length);
                fileOut.flush();
            }

            //массив байт для последнего 8-мибайтного блока
            byte[] lastBlock = new byte[passwordByte.length];

            //заполняем массив
            System.arraycopy(recidue, recidue.length - lastBlock.length, lastBlock, 0, lastBlock.length);

            //массив для расшифрованного посделнего блока
            byte[] littleResult = new byte[8];
            ProcessData(passwordByte, lastBlock, littleResult);
            System.out.println(Arrays.toString(lastBlock));
            System.out.println(Arrays.toString(littleResult));

            int lastBlockForDecryptSize = 0;
            byte[] differense = new byte[8];
            for (int i = 1; i < password.length(); i++) {
                differense[i] = (byte) (littleResult[i] - lastBlock[i]);
                if (Math.abs(differense[i]) == passwordByte[i]) lastBlockForDecryptSize++;
//                System.out.println(lastBlockForDecryptSize);
            }
            System.out.println(arraysCompare(differense, passwordByte));
            System.out.println(Arrays.toString(passwordByte));
            System.out.println(Arrays.toString(differense));


            //пишем в файл при расшифровке
            if (!encrypt) {
                int lastBlockSize = recidue.length - lastBlockForDecryptSize;
                fileOut.write(recidue, 0, lastBlockSize);
                fileOut.flush();

            }

            //пишем в файл при зашифровке
            if (encrypt) {
                fileOut.write(recidue, 0, recidue.length);
                fileOut.flush();
            }

        }

        fileIn.close();
        fileOut.close();
    }


    //читаем данные из буфера для чтения обрабатываем методом
    //ProcessData и формируем буфер для записи для дальней передачи в файл
    private void readFromBuffer(InputStream fromBufferReader) {
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
    }

    private int arraysCompare(byte[] a, byte[] b) {
        int count = 0;
        for (int i = 1; i < a.length; i++) {
            if (a[i] == b[i]) count++;
            if (a[i] != b[i]) count = 0;

        }
        return count;
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
                "This utility can encrypt and decrypt files.\n" +
                        "\n" +
                        "For program start, you need enter 4 parameters through a space:\n" +
                        "1. Input file name including path.\n" +
                        "2. Output file name including path.\n" +
                        "3. Mode: \"encrypt\" or \"decrypt\" without quotes.\n" +
                        "4. Password (8 characters).");
    }
}
