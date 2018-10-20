package com.lxgy.analysis;

import com.lxgy.analysis.track.ae.sdk.AnalyticsEngineSDK;
import com.lxgy.analysis.track.enums.OrderChargeTypeEnum;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AnalysisApplicationTests {

    @Test
    public void contextLoads() {
        boolean onOrderCharge = AnalyticsEngineSDK.onOrderCharge(OrderChargeTypeEnum.SUCCESS, "orderId001", "member001");
    }

}
