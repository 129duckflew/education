package cn.duckflew.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TypeNotReadCount
{
    private Integer msgType;
    private Integer notReadCount;
}
