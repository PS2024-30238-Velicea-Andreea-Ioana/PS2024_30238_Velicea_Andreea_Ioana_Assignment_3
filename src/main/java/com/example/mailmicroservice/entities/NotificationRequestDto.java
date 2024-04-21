package com.example.mailmicroservice.entities;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationRequestDto {
    private String id;
    private String name;
    private String email;
    private String action;
}
