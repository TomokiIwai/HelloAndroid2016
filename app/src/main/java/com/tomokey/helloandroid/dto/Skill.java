package com.tomokey.helloandroid.dto;

public class Skill {
    public String name;
    public String skill;

    public Skill(String n, String s) {
        name = n;
        skill = s;
    }

    public String getName() {
        return name;
    }

    public String getSkill() {
        return skill;
    }
}
