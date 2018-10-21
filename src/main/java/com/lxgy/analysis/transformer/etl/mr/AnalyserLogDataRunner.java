package com.lxgy.analysis.transformer.etl.mr;

import com.lxgy.analysis.transformer.common.EventLogConstants;
import com.lxgy.analysis.transformer.common.GlobalConstants;
import com.lxgy.analysis.transformer.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * @author Gryant
 */
public class AnalyserLogDataRunner implements Tool {

    /**
     * log util
     */
    private static final Logger logger = Logger.getLogger(AnalyserLogDataMapper.class);

    private Configuration conf = null;


    public static void main(String[] args) {

        try {

            ToolRunner.run(new Configuration(), new AnalyserLogDataRunner(), args);

        } catch (Exception e) {
            logger.error("执行日志解析异常", e);
        }

    }

    @Override
    public int run(String[] args) throws Exception {

        Configuration configuration = this.getConf();
        this.processArgs(configuration, args);

        Job job = Job.getInstance(configuration, "analyser_logdata");
        job.setJarByClass(AnalyserLogDataRunner.class);
        job.setMapperClass(AnalyserLogDataMapper.class);
        job.setMapOutputKeyClass(NullWritable.class);
        job.setMapOutputValueClass(Put.class);

        // 设置reducer配置
        // 1，集群上运行，达成jar
        // TableMapReduceUtil.initTableReducerJob(EventLogConstants.HBASE_NAME_EVENT_LOGS, null, job );
        // 2，本地运行，要求参数addDependencyJars 为false
        TableMapReduceUtil.initTableReducerJob(EventLogConstants.HBASE_NAME_EVENT_LOGS, null, job, null, null, null, null, false);
        job.setNumReduceTasks(0);

        // 设置输入路径
        this.setJarInputPaths(job);

        return job.waitForCompletion(true) ? 0 : -1;
    }

    /**
     * 设置job的输入路径
     *
     * @param job
     */
    private void setJarInputPaths(Job job) {
        Configuration conf = job.getConfiguration();
        FileSystem fs = null;

        try {

            fs = FileSystem.get(conf);
            String date = conf.get(GlobalConstants.RUNNING_DATE);
            Path inputPath = new Path("/logs/" + DateUtils.parseLong2String(DateUtils.parseString2Long(date), DateUtils.DATE_M_D_FORMAT + File.separator));
            if (!fs.exists(inputPath)) {
                throw new RuntimeException("file is not existed," + inputPath);
            }

            FileInputFormat.addInputPath(job, inputPath);
        } catch (Exception e) {
            logger.error("设置job的输入路径异常" + e);
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                    // nothing
                }
            }
        }
    }

    /**
     * 处理参数
     *
     * @param configuration
     * @param args
     */
    private void processArgs(Configuration configuration, String[] args) {

        String date = null;

        for (int i = 0; i < args.length; i++) {
            if ("-d".equals(args[i])) {
                if (i + 1 < args.length) {
                    date = args[++i];
                    break;
                }
            }
        }

        // date 是一个无效的时间数据，要求时间格式为：yyyy-MM-dd
        if (StringUtils.isBlank(date) || !DateUtils.isValidateRunningDate(date)) {
            date = DateUtils.getYesterday();
        }

        conf.set(GlobalConstants.RUNNING_DATE, date);
    }

    @Override
    public void setConf(Configuration conf) {
        this.conf = HBaseConfiguration.create(conf);
    }

    @Override
    public Configuration getConf() {
        return this.conf;
    }
}
