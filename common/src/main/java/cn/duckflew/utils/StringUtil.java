package cn.duckflew.utils;

import java.util.Random;

@SuppressWarnings("AlibabaClassMustHaveAuthor")
public class StringUtil
{

    public static String getRandomCode(int digit)
    {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        if (digit>0) {
            for (int i = 0; i < digit; i++) {
                sb.append((char)(random.nextInt(26)+'a'));
            }
        }
        return sb.toString();
    }
    public static String getRandomDigitCode(int digit)
    {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        if (digit>0) {
            for (int i = 0; i < digit; i++) {
                sb.append(random.nextInt(10));
            }
        }
        return sb.toString();
    }
    public static boolean isNullOrEmpty(String data)
    {
        return data==null||data.isEmpty();
    }

    public static String encodeCardId(String cardId)
    {
        if (isNullOrEmpty(cardId))return null;
        return cardId.substring(0,4) +"**********"+cardId.substring(14);
    }
}
