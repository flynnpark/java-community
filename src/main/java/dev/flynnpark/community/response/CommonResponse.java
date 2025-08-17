package dev.flynnpark.community.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> {
    private int code;
    private String message;
    private T result;

    public static <T> CommonResponse<T> success(int code, String message, T result) {
        return new CommonResponse<>(code, message, result);
    }

    public static <T> CommonResponse<T> success(T result) {
        return success(200, "Success", result);
    }

    public static <T> CommonResponse<T> success() {
        return success(200, "Success", null);
    }

    public static <T> CommonResponse<T> error(int code, String message) {
        return new CommonResponse<>(code, message, null);
    }
}
