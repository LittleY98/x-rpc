package third.yang.serialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LittleY
 * @date 2024/3/21
 * @description TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student {
    private String name;
    private Byte age;
    private Long id;
}
