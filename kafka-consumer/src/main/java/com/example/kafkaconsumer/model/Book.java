package com.example.kafkaconsumer.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Book {
    private Integer id;
    private String name;
    private String description;
    private boolean checked;
}
