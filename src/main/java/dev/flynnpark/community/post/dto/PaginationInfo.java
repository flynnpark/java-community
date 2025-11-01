package dev.flynnpark.community.post.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Page;

@Getter
@Builder
public class PaginationInfo {
    private int totalPages;
    private long totalElements;
    private int pageNumber;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;

    public static PaginationInfo from(Page<?> page) {
        return PaginationInfo.builder()
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }
}
