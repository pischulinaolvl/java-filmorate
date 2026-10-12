package ru.yandex.practicum.filmorate.repository.review;

import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public final class ReviewMapper {
    private ReviewMapper() {
    }

    public static ReviewDto mapToReviewDto(Review review) {
        if (review == null) {
            return null;
        }
        ReviewDto dto = new ReviewDto();
        dto.setReviewId(review.getReviewId());
        dto.setContent(review.getContent());
        dto.setIsPositive(review.getIsPositive());
        dto.setUserId(review.getUserId());
        dto.setFilmId(review.getFilmId());
        dto.setUseful(review.getUseful());
        return dto;
    }

    public static Review mapToReview(ReviewDto dto) {
        if (dto == null) {
            return null;
        }
        Review review = new Review();
        review.setReviewId(dto.getReviewId());
        review.setContent(dto.getContent());
        review.setIsPositive(dto.getIsPositive());
        review.setUserId(dto.getUserId());
        review.setFilmId(dto.getFilmId());
        review.setUseful(dto.getUseful() != null ? dto.getUseful() : 0);
        return review;


    }

    public static List<ReviewDto> mapToReviewDtoList(List<Review> reviews) {
        if (reviews == null) {
            return List.of();
        }
        return reviews.stream()
                .map(ReviewMapper::mapToReviewDto)
                .toList();
    }
}