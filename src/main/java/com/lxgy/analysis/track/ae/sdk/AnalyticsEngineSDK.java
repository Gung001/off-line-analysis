package com.lxgy.analysis.track.ae.sdk;

import com.lxgy.analysis.core.contants.ConstSymbol;
import com.lxgy.analysis.track.config.SdkConfig;
import com.lxgy.analysis.track.enums.OrderChargeTypeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 分析引擎SDK java服务器端数据收集
 *
 * @author Gryant
 */
public class AnalyticsEngineSDK {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsEngineSDK.class);

    /**
     * 触发订单支付成功/失败事件，发送事件数据到服务器
     *
     * @param typeEnum 订单类型
     * @param orderId  订单id
     * @param memberId 会员id
     * @return
     */
    public static boolean onOrderCharge(OrderChargeTypeEnum typeEnum, String orderId, String memberId) {

        boolean retFlag = false;

        try {

            log.debug("发送[" + typeEnum.getDesc() + "]事件数据到服务器,req={orderId:" + orderId + ",memberId=" + memberId + "}");
            if (StringUtils.isEmpty(orderId) || StringUtils.isEmpty(memberId)) {
                return retFlag;
            }

            Map<String, String> data = new HashMap<>();
            SdkConfig sdkConfig = new SdkConfig("http://data01/BfImg.gif", "java_server", "jdk", "1");
            data.put("u_mid", memberId);
            data.put("oid", orderId);
            data.put("c_time", String.valueOf(System.currentTimeMillis()));
            data.put("ver", sdkConfig.getVersion());
            data.put("pl", sdkConfig.getPlatformName());
            data.put("sdk", sdkConfig.getSdkName());
            if (OrderChargeTypeEnum.SUCCESS.getCode().equals(typeEnum.getCode())) {
                data.put("en", "e_cs");
            } else {
                data.put("en", "e_cr");
            }

            String buildUrl = buildUrl(sdkConfig.getAccessUrl(), data);
            log.debug("组装后的事件数据,url=" + buildUrl);

            // 发送url&将url添加到队列
            SendDataMonitor.addSendUrl(buildUrl);

            // 结果
            retFlag = true;
        } catch (Exception e) {

            log.error("触发订单支付成功/失败事件，发送事件数据到服务器异常", e);
        }

        return retFlag;
    }

    /**
     * 根据传入的参数构建url
     *
     * @param accessUrl
     * @param data
     * @return
     */
    private static String buildUrl(String accessUrl, Map<String, String> data) throws UnsupportedEncodingException {

        StringBuilder builder = new StringBuilder();

        builder.append(accessUrl).append(ConstSymbol.QUESTION_MARK);

        for (Map.Entry<String, String> entry : data.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
                log.debug("根据传入的参数构建url,参数[" + key + "]或值[" + value + "]无效");
                continue;
            }

            builder.append(key.trim())
                    .append(ConstSymbol.EQUALS_SIGN)
                    .append(URLEncoder.encode(value.trim(), "utf-8"))
                    .append(ConstSymbol.AND_MARK);
        }

        return builder.deleteCharAt(builder.length() - 1).toString();
    }

    public static void main(String[] args) {

        AnalyticsEngineSDK.onOrderCharge(OrderChargeTypeEnum.SUCCESS, "orderId001", "member001");
        AnalyticsEngineSDK.onOrderCharge(OrderChargeTypeEnum.REFUND, "orderId002", "member002");

    }
}
