package cn.duckflew.utils;


import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

public class CardIdUtilTest
{

    @Test
    public void getBirthday() throws ParseException
    {
        String cardId="421222200101100014";
        Date birthday = CardIdUtil.getBirthday(cardId);
        String sex = CardIdUtil.getSex(cardId);
        Integer age = CardIdUtil.getAge(cardId);
        System.out.println(birthday);
        System.out.println(sex);
        System.out.println(age);
    }
}