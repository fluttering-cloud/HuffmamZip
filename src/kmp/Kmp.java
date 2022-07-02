package kmp;

public class Kmp {


    public int kmp(String str, String subStr) {
        int[] next = kmpNext(subStr);
        return kmpSearch(str,subStr,next);
    }

    public int kmpSearch(String str,String subStr,int[] next) {
        for (int i = 0,j = 0; i < str.length();i++) {
            while (j > 0 && str.charAt(i) != subStr.charAt(j)) {
                j = next[j - 1];
            }
            if (str.charAt(i) == subStr.charAt(j)) {
                j++;
            }
            if (j == subStr.length()) {
                return i - j + 1;
            }
        }
        return -1;
    }

    /**
     * KMP的所有移动都是根据next表来进行的
     * next表中记录着字符串从0到当前位置的子串中最长相等前后缀的长度
     * @param sbuStr
     * @return
     */
    private int[] kmpNext(String sbuStr) {
        int[] next = new int[sbuStr.length()];
        if (sbuStr.length() == 1) next[0] = 0;
        for (int i = 1,j = 0; i < sbuStr.length(); i++) {
            // i 指向后缀末尾
            // j 指向前缀的末尾，同时也代表当前前缀的长度
            while (j > 0 && sbuStr.charAt(j) != sbuStr.charAt(i)) {
                j = next[j - 1];
            }
            if (sbuStr.charAt(j) != sbuStr.charAt(i)) {
                j++;
            }
            next[i] = j;
        }
        return next;
    }
}
