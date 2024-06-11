package com.goal.order.feign;

import com.goal.order.domain.vo.CouponRecordVO;
import com.goal.utils.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("coupon-service")
public interface CouponFeignService {

    @GetMapping("api/${app.config.api.version}/coupon_record/detail/{record_id}")
    Result<CouponRecordVO> findUserCouponRecordById(@PathVariable("record_id") long recordId);

}
