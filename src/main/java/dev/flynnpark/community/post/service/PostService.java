package dev.flynnpark.community.post.service;

import dev.flynnpark.community.post.dto.PostCreateRequest;
import dev.flynnpark.community.post.dto.PostResponse;
import dev.flynnpark.community.post.entity.Post;
import dev.flynnpark.community.post.repository.PostRepository;
import dev.flynnpark.community.user.entity.User;
import dev.flynnpark.community.user.dto.UserResponseForPost;
import dev.flynnpark.community.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse createPost(PostCreateRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userEmail));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .build();

        Post savedPost = postRepository.save(post);

        return PostResponse.builder()
                .id(savedPost.getId())
                .title(savedPost.getTitle())
                .content(savedPost.getContent())
                .author(UserResponseForPost.builder()
                        .id(savedPost.getAuthor().getId())
                        .nickname(savedPost.getAuthor().getNickname())
                        .roles(savedPost.getAuthor().getRoles())
                        .build())
                .createdAt(savedPost.getCreatedAt())
                .updatedAt(savedPost.getUpdatedAt())
                .build();
    }

    public Page<PostResponse> list(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(post -> PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(UserResponseForPost.builder()
                        .id(post.getAuthor().getId())
                        .nickname(post.getAuthor().getNickname())
                        .roles(post.getAuthor().getRoles())
                        .build())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build());
    }

    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + id));

        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(UserResponseForPost.builder()
                        .id(post.getAuthor().getId())
                        .nickname(post.getAuthor().getNickname())
                        .roles(post.getAuthor().getRoles())
                        .build())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
