package com.practice.kafkaStream;

import org.apache.kafka.streams.processor.Processor;
import org.apache.kafka.streams.processor.ProcessorContext;

/**
 * @Description 日志类
 * @Author fuchen
 * @Date 2019/12/5 20:36
 * Version 1.0
 */
public class LogProcessor implements Processor<byte[], byte[]> {

    // 上下文信息
    private ProcessorContext context;

    @Override
    public void init(ProcessorContext processorContext) {
        this.context = processorContext;
    }

    // 用于处理数据
    @Override
    public void process(byte[] bytes, byte[] bytes2) {

    }

    @Override
    public void punctuate(long l) {

    }

    @Override
    public void close() {

    }
}
