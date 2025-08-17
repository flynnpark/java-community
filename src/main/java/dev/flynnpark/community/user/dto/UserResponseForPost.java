package dev.flynnpark.community.user.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class UserResponseForPost {
    private Long id;
    private String nickname;
    private List<String> roles;
}
