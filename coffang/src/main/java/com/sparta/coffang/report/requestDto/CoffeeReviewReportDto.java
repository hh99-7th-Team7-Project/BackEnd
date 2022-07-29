package com.sparta.coffang.report.requestDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CoffeeReviewReportDto {
    private Long userId;
    private Long coffeeId;
    private Long coffeeReviewId;
}
