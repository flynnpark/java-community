package dev.flynnpark.community.post.service;

import dev.flynnpark.community.post.dto.PaginatedPostResponse;
import dev.flynnpark.community.post.dto.PostCreateRequest;
import dev.flynnpark.community.post.dto.PostResponse;
import dev.flynnpark.community.post.entity.Post;
import dev.flynnpark.community.post.repository.PostRepository;
import dev.flynnpark.community.user.entity.User;
import dev.flynnpark.community.user.dto.UserResponseForPost;
import dev.flynnpark.community.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.flynnpark.community.post.dto.PostUpdateRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponse createPost(PostCreateRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("이메일 " + userEmail + "을(를) 가진 사용자를 찾을 수 없습니다."));

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

    public PaginatedPostResponse list(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        Page<PostResponse> postResponses = posts.map(post -> PostResponse.builder()
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
        return PaginatedPostResponse.of(postResponses);
    }

    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ID가 " + id + "인 게시글을 찾을 수 없습니다."));

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

    @Transactional
    public PostResponse updatePost(Long id, PostUpdateRequest request, String userEmail) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ID가 " + id + "인 게시글을 찾을 수 없습니다."));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("이메일 " + userEmail + "을(를) 가진 사용자를 찾을 수 없습니다."));

        if (!post.getAuthor().equals(user)) {
            throw new AccessDeniedException("게시글을 수정할 권한이 없습니다.");
        }

        post.update(request.getTitle(), request.getContent());

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
