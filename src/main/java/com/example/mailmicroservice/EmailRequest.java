package com.example.mailmicroservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class EmailRequest {
    private String to;
    private String subject;
    private String body;
}
