package fun.keepon.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author LittleY
 * @description 测试用 学生实体类
 * @date 2024/2/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Student implements Serializable {
    private Long id;
    private Integer age;

}
