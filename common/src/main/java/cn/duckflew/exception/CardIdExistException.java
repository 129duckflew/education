package cn.duckflew.exception;


import lombok.Getter;
import lombok.Setter;

public class CardIdExistException extends RuntimeException
{
    @Getter
    @Setter
    private String cardId;
    public CardIdExistException(String message, String cardId)
    {
        super(message);
        setCardId(cardId);
    }
}
