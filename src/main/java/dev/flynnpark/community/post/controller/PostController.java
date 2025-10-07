package dev.flynnpark.community.post.controller;

import dev.flynnpark.community.post.dto.PostCreateRequest;
import dev.flynnpark.community.post.dto.PostResponse;
import dev.flynnpark.community.post.service.PostService;
import dev.flynnpark.community.response.CommonResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<CommonResponse<PostResponse>> createPost(@Valid @RequestBody PostCreateRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName(); // 현재 로그인된 사용자의 이메일

        PostResponse postResponse = postService.createPost(request, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success(HttpStatus.CREATED.value(), "게시글이 성공적으로 작성되었습니다.", postResponse));
    }

    @GetMapping
    public ResponseEntity<CommonResponse<Page<PostResponse>>> listPosts(Pageable pageable) {
        Page<PostResponse> posts = postService.list(pageable);
        return ResponseEntity.ok(CommonResponse.success(HttpStatus.OK.value(), "게시글 목록이 성공적으로 조회되었습니다.", posts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<PostResponse>> getPost(@PathVariable Long id) {
        PostResponse postResponse = postService.getPost(id);
        return ResponseEntity.ok(CommonResponse.success(HttpStatus.OK.value(), "게시글이 성공적으로 조회되었습니다.", postResponse));
    }
}
                
