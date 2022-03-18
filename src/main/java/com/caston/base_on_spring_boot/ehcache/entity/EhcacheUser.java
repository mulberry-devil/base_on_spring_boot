package com.caston.base_on_spring_boot.ehcache.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EhcacheUser implements Serializable {
    private String id;
    private String name;
    private Integer age;
}
