package com.goal.order.feign;

import com.goal.order.domain.dto.CouponLockDTO;
import com.goal.order.domain.vo.CouponRecordVO;
import com.goal.utils.Result;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("coupon-service")
public interface CouponFeignService {

    @GetMapping("api/${app.config.api.version}/coupon_record/detail/{record_id}")
    Result<CouponRecordVO> findUserCouponRecordById(@PathVariable("record_id") long recordId);

    @PostMapping("api/${app.config.api.version}/coupon_record/lock_record")
    Result lockCouponRecords(@RequestBody CouponLockDTO couponLockDTO);

}
