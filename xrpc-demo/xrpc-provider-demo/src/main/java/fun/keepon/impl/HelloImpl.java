package fun.keepon.impl;

import fun.keepon.api.Hello;
import fun.keepon.bean.Student;

/**
 * @author LittleY
 * @description TODO
 * @date 2024/2/6
 */
public class HelloImpl implements Hello {
    @Override
    public Student generateStudent(long id, String name, int age) {
        return new Student(id, name, age);
    }
}
