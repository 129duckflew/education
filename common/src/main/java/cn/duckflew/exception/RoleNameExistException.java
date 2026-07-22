package cn.duckflew.exception;

import lombok.Getter;

@Getter
public class RoleNameExistException extends RuntimeException
{

    private String roleName;
    public RoleNameExistException(String message,String roleName)
    {
        super(message);
        this.roleName=roleName;
    }

}
