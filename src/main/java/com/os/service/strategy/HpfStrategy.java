package com.os.service.strategy;

import com.os.model.PCB;
import com.os.service.Dispatcher;
import java.util.List;

/**
 * 抢占式高优先级优先算法 (Highest Priority First)
 * 优先级高的可以随时踢掉正在运行的低优先级进程（数字越小代表优先级越高）
 */
public class HpfStrategy implements SchedulingStrategy {

    @Override
    public void executeTick(Dispatcher dispatcher) {
        PCB running = dispatcher.getRunningProcess();
        List<PCB> queue = dispatcher.getReadyQueue();

        // 1. 实现抢占机制：每次滴答都把当前进程“假装”扔回排队大军中去竞争
        if (running != null) {
            running.setState(PCB.READY);
            queue.add(running);
            dispatcher.setRunningProcess(null);
        }

        // 2. 重新选拔：在所有人里找优先级最高的（ priority 数字越小优先级越高）
        if (!queue.isEmpty()) {
            // 利用 Comparator 排序，数字小的排最前面
            queue.sort((p1, p2) -> Integer.compare(p1.getPriority(), p2.getPriority()));
            
            running = queue.remove(0);
            running.setState(PCB.RUNNING);
            dispatcher.setRunningProcess(running);
        }

        // 3. 执行
        if (running != null) {
            running.setRunningTime(running.getRunningTime() + 1);

            if (running.getRunningTime() >= running.getTotalTime()) {
                dispatcher.getMemoryManager().deallocate(running.getPid());
                running.setState(PCB.DEAD);
                dispatcher.setRunningProcess(null);
            }
        }
    }
}