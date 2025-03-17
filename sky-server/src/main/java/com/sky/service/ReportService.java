package com.sky.service;

import com.sky.vo.TurnoverReportVO;
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
}
