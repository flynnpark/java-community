package dev.flynnpark.community.post.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
public class PaginatedPostResponse {
    private PaginationInfo pagination;
    private List<PostResponse> items;

    public static PaginatedPostResponse of(Page<PostResponse> page) {
        List<PostResponse> postResponses = page.getContent();
        PaginationInfo paginationInfo = PaginationInfo.from(page);

        return PaginatedPostResponse.builder()
                .pagination(paginationInfo)
                .items(postResponses)
                .build();
    }
}
