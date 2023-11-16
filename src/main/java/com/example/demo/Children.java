package com.example.demo;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Children {

    private int id;
    private String name;
    private String age;
    private String location;
    private Boolean sex;
    private Character nickName;

    private List<String> list;

    private List<Hobbie> hobbieList;

    private List<Integer> integerList;

    public Children(int id, String name, String age, String location, Boolean sex, Character nickName, List<Hobbie> hobbieList, List<Integer> integerList) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.location = location;
        this.sex = sex;
        this.nickName = nickName;
        this.hobbieList = hobbieList;
        this.integerList = integerList;
    }
}
