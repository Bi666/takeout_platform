package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public interface ReportService {

    /**
     * Statistics turnover over a period of time
     * @param begin
     * @param end
     * @return
     */
    TurnoverReportVO getTurnoverReport(LocalDate begin, LocalDate end);

    /**
     * Statistics user over a period of time
     * @param begin
     * @param end
     * @return
     */
    UserReportVO getUserReport(LocalDate begin, LocalDate end);

    /**
     * Statistics order over a period of time
     * @param begin
     * @param end
     * @return
     */
    OrderReportVO getOrderReport(LocalDate begin, LocalDate end);
}
