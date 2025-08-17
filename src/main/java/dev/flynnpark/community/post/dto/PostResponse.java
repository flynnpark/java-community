package dev.flynnpark.community.post.dto;

import dev.flynnpark.community.user.dto.UserResponseForPost;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private UserResponseForPost author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
