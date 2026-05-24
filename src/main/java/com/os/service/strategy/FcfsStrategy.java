package com.os.service.strategy;

import com.os.model.PCB;
import com.os.service.Dispatcher;
import java.util.List;

/**
 * 先来先服务算法 (First Come First Serve)
 * 一旦霸占CPU，不到死或者阻塞就绝对不放手（非抢占式）
 */
public class FcfsStrategy implements SchedulingStrategy {

    @Override
    public void executeTick(Dispatcher dispatcher) {
        PCB running = dispatcher.getRunningProcess();
        List<PCB> queue = dispatcher.getReadyQueue();

        // 如果没有正在运行，选第一个创建进来的运行
        if (running == null && !queue.isEmpty()) {
            running = queue.remove(0);
            running.setState(PCB.RUNNING);
            dispatcher.setRunningProcess(running);
        }

        // 执行
        if (running != null) {
            running.setRunningTime(running.getRunningTime() + 1);

            // 判断是否执行完毕
            if (running.getRunningTime() >= running.getTotalTime()) {
                dispatcher.getMemoryManager().deallocate(running.getPid());
                running.setState(PCB.DEAD);
                dispatcher.setRunningProcess(null);
            }
            // FCFS 不主动交出 CPU，所以没有任何 queue.add() 操作
        }
    }
}