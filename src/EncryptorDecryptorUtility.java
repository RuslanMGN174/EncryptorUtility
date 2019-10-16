import java.io.*;
import java.util.Arrays;

public class EncryptorDecryptorUtility {
    private int bufferSize = 64_000;
    private byte[] data = new byte[8];
    private byte[] result = new byte[8];

    void encrypt(String fileFrom, String fileTo, String password, boolean encrypt) throws IOException {

        //Потоки чтения и записи
        InputStream fileIn = new BufferedInputStream(new FileInputStream(fileFrom));
        BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(fileTo));

        //Получаем байты пароля
        byte[] passwordByte = password.getBytes();

        //буферы чтения и записи
        byte[] buffer = new byte[bufferSize];
        byte[] writeBuffer = new byte[bufferSize];

        //Поток чтения из буфера
        InputStream fromBufferReader = new ByteArrayInputStream(buffer);


        //читаем данные из файла в буфер для чтения
        while (fileIn.available() > 0) {
            int countBytes = fileIn.read(buffer);
            int index = 0;

            //читаем данные из буфера для чтения обрабатываем методом
            //ProcessData и формируем буфер для записи для дальней передачи в файл
            while (fromBufferReader.available() > 0) {
                fromBufferReader.read(data, 0, 8);
                ProcessData(passwordByte, data, result);
                System.arraycopy(result, 0, writeBuffer, index, result.length);
                index += result.length;
            }

            //когда размерфайла или остаток для чтения меньше буфера
            if (countBytes < bufferSize) {

                //вычисляем количество зашифрованных байт
                int fileSizeExtra;
                if (countBytes % password.length() != 0) {
                    fileSizeExtra = countBytes / passwordByte.length * passwordByte.length + passwordByte.length;
                } else {
                    fileSizeExtra = countBytes / passwordByte.length * passwordByte.length;
                }

                //отдельный буфер для зашифрованных байт
                byte[] extraBuffer = new byte[fileSizeExtra];


                //массив байт для последнего 8-мибайтного блока
                byte[] lastBlock = new byte[passwordByte.length];


                //заполняем буффер
                System.arraycopy(writeBuffer, 0, extraBuffer, 0, extraBuffer.length);
//                System.out.println(Arrays.toString(extraBuffer));

                //заполняем массив
                System.arraycopy(extraBuffer, extraBuffer.length - lastBlock.length, lastBlock, 0, lastBlock.length);
//                System.out.println(Arrays.toString(lastBlock));

                //массив для расшифрованного посделнего блока
                byte[] littleResult = new byte[8];
                ProcessData(passwordByte, lastBlock, littleResult);

//                int lastBlockForDecryptSize = 0;
//                byte[] differense = new byte[8];
//                for (int i = 1; i < password.length(); i++) {
//                    differense[i] = (byte) (lastBlock[i]- littleResult[i]);
//                    if (Math.abs(differense[i]) == passwordByte[i]) lastBlockForDecryptSize++;
//                }


                //пишем в файл при расшифровке
                if (!encrypt) {
                    int lastBlockSize = extraBuffer.length - 2;
                    fileOut.write(extraBuffer, 0, lastBlockSize);
                }

                //пишем в файл при зашифровке
                if (encrypt) {
                    fileOut.write(extraBuffer, 0, extraBuffer.length);
                }

            } else {
                fileOut.write(writeBuffer, 0, bufferSize);
            }

            fileOut.flush();
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
                "This utility can encrypt and decrypt files.\n" +
                        "\n" +
                        "For program start, you need enter 4 parameters through a space:\n" +
                        "1. Input file name including path.\n" +
                        "2. Output file name including path.\n" +
                        "3. Mode: \"encrypt\" or \"decrypt\" without quotes.\n" +
                        "4. Password (8 characters).");
    }
}