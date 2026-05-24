package com.os.service;

import com.os.model.PCB;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.os.service.strategy.SchedulingStrategy;
import com.os.service.strategy.RrStrategy;
import com.os.service.strategy.FcfsStrategy;
import com.os.service.strategy.HpfStrategy;
import lombok.Getter;
import lombok.Setter;

@Service
@EnableScheduling
@Getter
@Setter
public class Dispatcher {
    private List<PCB> readyQueue = new LinkedList<>();
    private PCB runningProcess = null;
    
    // 当前启用的调度策略
    private SchedulingStrategy currentStrategy = new RrStrategy();
    private String currentStrategyName = "RR";

    @Autowired
    private MemoryManager memoryManager;

    // 动态切换算法
    public synchronized void changeStrategy(String algo) {
        switch (algo.toUpperCase()) {
            case "FCFS": 
                this.currentStrategy = new FcfsStrategy(); 
                this.currentStrategyName = "FCFS"; 
                break;
            case "HPF": 
                this.currentStrategy = new HpfStrategy(); 
                this.currentStrategyName = "HPF"; 
                break;
            case "RR": 
            default: 
                this.currentStrategy = new RrStrategy(); 
                this.currentStrategyName = "RR"; 
                break;
        }
    }

    // 添加新进程
    public synchronized boolean addProcess(PCB pcb) {
        // 尝试分配内存
        if (memoryManager.allocate(pcb.getPid(), pcb.getMemoryNeed())) {
            pcb.setState(PCB.READY);
            readyQueue.add(pcb);
            System.out.println("进程创建成功，PID: " + pcb.getPid());
            return true;
        }
        System.out.println("内存不足，进程创建失败");
        return false;
    }

    // 模拟 CPU 提供时间片的心跳（每1秒钟滴答一次）
    @Scheduled(fixedRate = 1000)
    public synchronized void tick() {
        if (currentStrategy != null) {
            // 调用多态策略对象执行统一的排队与CPU处理抽象口
            currentStrategy.executeTick(this);
        }
    }

    // 撤销/强制杀死进程
    public synchronized boolean killProcess(int pid) {
        // 如果正在运行
        if (runningProcess != null && runningProcess.getPid() == pid) {
            memoryManager.deallocate(pid);
            runningProcess = null;
            return true;
        }
        // 如果在就绪队列中
        boolean removed = readyQueue.removeIf(p -> p.getPid() == pid);
        if (removed) {
            memoryManager.deallocate(pid);
            return true;
        }
        return false;
    }

    // 进程通信：发送消息给指定PID
    public synchronized boolean sendMessage(int toPid, String message) {
        if (runningProcess != null && runningProcess.getPid() == toPid) {
            runningProcess.getMessages().add(message);
            return true;
        }
        for (PCB pcb : readyQueue) {
            if (pcb.getPid() == toPid) {
                pcb.getMessages().add(message);
                return true;
            }
        }
        return false;
    }

    // 组合队列以便前端展示
    public synchronized List<PCB> getStatusList() {
        List<PCB> list = new ArrayList<>();
        if (runningProcess != null) {
            list.add(runningProcess); // 正在运行的放最上面
        }
        list.addAll(readyQueue);
        return list;
    }
}