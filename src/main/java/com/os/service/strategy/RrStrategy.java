package com.os.service.strategy;

import com.os.model.PCB;
import com.os.service.Dispatcher;
import java.util.List;

/**
 * 时间片轮转算法 (Round Robin)
 * 每次执行1个时间单位，如果未完成则重新放入队尾
 */
public class RrStrategy implements SchedulingStrategy {

    @Override
    public void executeTick(Dispatcher dispatcher) {
        PCB running = dispatcher.getRunningProcess();
        List<PCB> queue = dispatcher.getReadyQueue();

        // 1. 如果当前没有运行的进程，且就绪队列不为空，则调度一个出来
        if (running == null && !queue.isEmpty()) {
            running = queue.remove(0);
            running.setState(PCB.RUNNING);
            dispatcher.setRunningProcess(running);
        }

        // 2. 模拟1个单位周期的CPU执行
        if (running != null) {
            running.setRunningTime(running.getRunningTime() + 1);

            if (running.getRunningTime() >= running.getTotalTime()) {
                // 运行完毕，释放内存，标记死亡
                dispatcher.getMemoryManager().deallocate(running.getPid());
                running.setState(PCB.DEAD);
                dispatcher.setRunningProcess(null);
            } else {
                // RR 核心逻辑：时间片到达（这里设定为1秒），如果没执行完，剥夺CPU并扔回队尾排队
                running.setState(PCB.READY);
                queue.add(running);
                dispatcher.setRunningProcess(null);
            }
        }
    }
}