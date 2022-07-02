package zip.huffman;

import com.sun.corba.se.impl.orbutil.ObjectWriter;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Zip {

    /**
     * 压缩文件的方法
     * @param srcFile
     * @param destFile
     * 该方法会将一个源文件压缩成一个zip文件
     */
    public static void zip(String srcFile,String destFile) {
        InputStream is = null;
        FileOutputStream os = null;
        ObjectOutputStream oos = null;
        byte[] zipBytes = null;
        HuffmanZipInfo huffmanZipInfo = new HuffmanZipInfo(new HashMap<Byte,String>(), 0);
        try {
            is = new FileInputStream(srcFile);
            byte[] srcBytes = new byte[is.available()];
            is.read(srcBytes);
            zipBytes = huffmanZip(srcBytes, huffmanZipInfo);
            // 将压缩结果写入文件
            os = new FileOutputStream(destFile);
            oos = new ObjectOutputStream(os);
            oos.writeObject(huffmanZipInfo);
            oos.writeObject(zipBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                oos.close();
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 采用赫夫曼编码对源数据进行压缩
     * @param srcBytes
     * @param huffmanZipInfo
     * @return
     */
    private static byte[] huffmanZip(byte[] srcBytes,HuffmanZipInfo huffmanZipInfo) {
        Map<Byte, String> huffmanCodes = huffmanZipInfo.getHuffmanCodes();
        StringBuffer buffer = new StringBuffer();
        // 对源文件出现过的字节进行赫夫曼编码，并把编码结果存入 huffmanCodes
        HuffmanNode huffmanTree = HuffmanNode.createHuffmanTree(srcBytes);
        HuffmanNode.huffmanCodes(huffmanTree,new StringBuffer(),huffmanCodes);
        // 对源目标进行赫夫曼编码
        for (byte b : srcBytes) {
            buffer.append(huffmanCodes.get(b));
        }
        // 将赫夫曼编码转换为字节进行传输
        int len;
        if (buffer.length() % 8 == 0) {
            // 编码后刚好 8 位，不用进行特殊处理，可以全部正常转换
            len = buffer.length() / 8;
        }else {
            huffmanZipInfo.setLastHuffmanByteCodesLength(buffer.length() % 8);
            len = buffer.length() / 8 +1;
        }
        byte[] huffmanByteCodes = new byte[len];
        int index = 0;
        for (int i = 0; i < buffer.length(); i+=8) {
            if (i + 8 > buffer.length()) {
                huffmanByteCodes[index++] = (byte)Integer.parseInt(buffer.substring(i),2);
            }else {
                huffmanByteCodes[index++] = (byte)Integer.parseInt(buffer.substring(i,i+8),2);
            }
        }
        return  huffmanByteCodes;
    }

    /**
     * 解压的方法
     * @param srcFile
     * @param destFile
     */
    public static void unzip(String srcFile,String destFile) {
        FileInputStream fis = null;
        ObjectInputStream is = null;
        OutputStream os = null;
        byte[] unzipBytes = null;
        try {
            fis = new FileInputStream(srcFile);
            is = new ObjectInputStream(fis);
            HuffmanZipInfo huffmanZipInfo = (HuffmanZipInfo)is.readObject();
            unzipBytes = (byte[]) is.readObject();
            unzipBytes = decode(unzipBytes,huffmanZipInfo);
            os = new FileOutputStream(destFile);
            os.write(unzipBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                os.close();
                is.close();
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 对压缩文件进行解压
     * @param unzipBytes  每个字节及其对应的赫夫曼编码
     * @param huffmanZipInfo 源数据压缩时的一些记录，主要用于解压
     */
    private static byte[] decode(byte[] unzipBytes,HuffmanZipInfo huffmanZipInfo) {
        Map<Byte, String> huffmanCodes = huffmanZipInfo.getHuffmanCodes();
        Integer lastHuffmanByteCodesLength = huffmanZipInfo.getLastHuffmanByteCodesLength();
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < unzipBytes.length; i++) {
            String s = byteToBitString(unzipBytes[i]);
            if (lastHuffmanByteCodesLength != 0 && i == unzipBytes.length - 1) {
                s = s.substring(s.length() - lastHuffmanByteCodesLength);
            }
            buffer.append(s);
        }
        byte[] srcBytes = huffmanCodesToSrcBytes(huffmanCodes, buffer.toString());
        return srcBytes;
    }

    /**
     * 字节类型的对象转换为对应的二进制串
     * @param b
     * @return
     * 关于flag的解释
     *   我们在将
     * 注意1：在 java 内只有有符号数，所有全部数都是用补码表示的
     * 注意2：正数的化，能用多少位二进制数表示就用多少（高位不补0）
     *       负数的化都用16位二进制表示（高位全1）
     */
    private static String byteToBitString(byte b) {
        int temp = b;
        temp |= 256;
        String bitString = Integer.toBinaryString(temp);
        return bitString.substring(bitString.length() - 8);
    }

    /**
     * 将赫夫曼编码变为源字节数组
     * @param huffmanCodes
     * @param huffmanStr
     * @return
     */
    private static byte[] huffmanCodesToSrcBytes(Map<Byte,String> huffmanCodes, String huffmanStr) {
        int index = 0;
        ArrayList<Byte> bytes = new ArrayList<>();
        while (index < huffmanStr.length()) {
            for (Map.Entry<Byte, String> byteStringEntry : huffmanCodes.entrySet()) {
                String matcherStr = byteStringEntry.getValue();
                if (matcherStr.length() + index <= huffmanStr.length()) {
                    String substring = huffmanStr.substring(index, index + matcherStr.length());
                    if (matcherStr.equals(substring)) {
                        index = index + matcherStr.length();
                        bytes.add(byteStringEntry.getKey());
                        break;
                    }
                }
            }
        }

        byte[] srcBytes = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            srcBytes[i] = bytes.get(i);
        }
        return srcBytes;
    }


}

/**
 * 这个类用于存放源数据进行赫夫曼编码后的相关数据，用于解码
 */
class HuffmanZipInfo implements Serializable{
    /**
     * Map<Byte,String> huffmanCodes
     * huffmanCodes 用于存储源数据中出现过的字节及每个字节对于的赫夫曼编码
     * Key : 源数据中出现过的字节
     * value : 字节对于的编码；
     */
     private Map<Byte,String> huffmanCodes;
     private Integer lastHuffmanByteCodesLength;

    HuffmanZipInfo(Map<Byte,String> huffmanCodes,Integer lastHuffmanByteCodesLength) {
        this.huffmanCodes = huffmanCodes;
        this.lastHuffmanByteCodesLength = lastHuffmanByteCodesLength;
    }

    public Map<Byte, String> getHuffmanCodes() {
        return huffmanCodes;
    }

    public void setHuffmanCodes(Map<Byte, String> huffmanCodes) {
        this.huffmanCodes = huffmanCodes;
    }

    public Integer getLastHuffmanByteCodesLength() {
        return lastHuffmanByteCodesLength;
    }

    public void setLastHuffmanByteCodesLength(Integer lastHuffmanByteCodesLength) {
        this.lastHuffmanByteCodesLength = lastHuffmanByteCodesLength;
    }
}



