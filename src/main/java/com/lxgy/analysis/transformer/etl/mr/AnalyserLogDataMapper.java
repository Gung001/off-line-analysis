package com.lxgy.analysis.transformer.etl.mr;

import com.lxgy.analysis.transformer.common.EventLogConstants;
import com.lxgy.analysis.transformer.utils.LoggerUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.zip.CRC32;

/**
 * @author Gryant
 */
public class AnalyserLogDataMapper extends Mapper<Object, Text, NullWritable, Put> {

    /**
     * log util
     */
    private static final Logger logger = Logger.getLogger(AnalyserLogDataMapper.class);

    /**
     * 统计
     */
    private int inputRecords, filterRecords, outputRecords;

    private byte[] family = Bytes.toBytes(EventLogConstants.EVENT_LOGS_FAMILY_NAME);

    private CRC32 crc32 = new CRC32();

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {

        this.inputRecords++;
        logger.debug("Analyse data," + value);

        try {

            Map<String, String> handleLog = LoggerUtil.handleLog(value.toString());
            if (handleLog == null || handleLog.isEmpty()) {
                this.filterRecords++;
                logger.debug("handleLog fail,param=" + value.toString());
                return;
            }

            String eventAliasName = handleLog.get(EventLogConstants.LOG_COLUMN_NAME_EVENT_NAME);
            EventLogConstants.EventEnum event = EventLogConstants.EventEnum.valueOfAlias(eventAliasName);
            switch (event) {
                case EVENT:
                case LAUNCH:
                case PAGEVIEW:
                case CHARGEREFUND:
                case CHARGEREQUEST:
                case CHARGESUCCESS:
                    handleData(handleLog, context, event);
                    this.outputRecords++;
                    break;
                default:
                    this.filterRecords++;
                    logger.debug("event cannot be parsed,event name;" + eventAliasName);
            }
        } catch (Exception e) {
            this.filterRecords++;
            logger.error("Analyse data error", e);
        }
    }

    /**
     * 具体处理数据的方法
     *
     * @param clientInfo
     * @param context
     * @param event
     */
    private void handleData(Map<String, String> clientInfo, Context context, EventLogConstants.EventEnum event) throws IOException, InterruptedException {

        String uuid = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_UUID);
        String mid = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_MEMBER_ID);
        String serverTime = clientInfo.get(EventLogConstants.LOG_COLUMN_NAME_SERVER_TIME);

        // 服务器时间不能为空
        if (StringUtils.isBlank(serverTime)) {
            logger.debug("handleData.serverTime is null");
            this.filterRecords++;
            return;
        }

        // 浏览器信息去掉
        clientInfo.remove(EventLogConstants.LOG_COLUMN_NAME_USER_AGENT);
        // timestamp +(uuid+mid+eventName).crc
        String rowKey = generateRowKey(uuid, mid, serverTime, event);
        Put put = new Put(Bytes.toBytes(rowKey));
        for (Map.Entry<String, String> entry : clientInfo.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isBlank(key) || StringUtils.isBlank(value)) {
                continue;
            }

            put.add(family, Bytes.toBytes(key), Bytes.toBytes(value));
        }

        context.write(NullWritable.get(), put);
        this.outputRecords++;
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        super.cleanup(context);
        logger.debug("input:" + inputRecords + ",output:" + outputRecords + ",fail:" + filterRecords);
    }

    /**
     * 生成 rowKey
     *
     * @param uuid
     * @param mid
     * @param serverTime
     * @param event
     * @return
     */
    private String generateRowKey(String uuid, String mid, String serverTime, EventLogConstants.EventEnum event) {

        StringBuilder builder = new StringBuilder();
        builder.append(serverTime).append("_");

        this.crc32.reset();
        if (StringUtils.isNotBlank(uuid)) {
            this.crc32.update(uuid.getBytes());
        }

        if (StringUtils.isNotBlank(mid)) {
            this.crc32.update(mid.getBytes());
        }

        this.crc32.update(event.alias.getBytes());

        builder.append(this.crc32.getValue() % 100000000L);

        return builder.toString();
    }


}
