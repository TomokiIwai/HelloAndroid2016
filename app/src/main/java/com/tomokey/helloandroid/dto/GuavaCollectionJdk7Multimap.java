package com.tomokey.helloandroid.dto;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GuavaCollectionJdk7Multimap {
    public void hoge() {
        final List<Skill> data = ImmutableList.of(
                new Skill("tom", "java"),
                new Skill("tom", "scala"),
                new Skill("jack", "html")
        );

        Map<String, Collection<String>> standard = new HashMap<>();
        for (Skill each : data) {
            if (!standard.containsKey(each.name)) {
                standard.put(each.name, new ArrayList<String>());
            }

            standard.get(each.name).add(each.skill);
        }

        Map<String, List<String>> collect
                = Stream.of(data).collect(
                Collectors.groupingBy(
                        Skill::getName,
                        Collectors.mapping(Skill::getSkill, Collectors.toList())
                )
        );

        Multimap<String, Skill> maps = ArrayListMultimap.create();
        maps.put("tom", new Skill("", ""));
        maps.put("tom", new Skill("", ""));
        maps.put("tom", new Skill("", ""));
    }
}
