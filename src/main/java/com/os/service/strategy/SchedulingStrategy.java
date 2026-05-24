package com.os.service.strategy;

import com.os.service.Dispatcher;

/**
 * 调度算法策略接口
 * 使用策略模式 (Strategy Pattern) 来实现代码的解耦与高模块化
 */
public interface SchedulingStrategy {
    void executeTick(Dispatcher dispatcher);
}