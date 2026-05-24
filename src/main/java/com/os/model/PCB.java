package com.os.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class PCB {
    // 状态常量
    public static final int CREATED = 0;
    public static final int READY = 1;
    public static final int BLOCK = 2;
    public static final int RUNNING = 3;
    public static final int DEAD = 4;

    private static int pidSeq = 1;

    private int pid;
    private int state;
    
    // CPU时间相关
    private int totalTime;
    private int runningTime;
    private int priority;
    
    // 资源需求(A,B,C)
    private int needA;
    private int needB;
    private int needC;
    private int getA;
    private int getB;
    private int getC;

    // 内存需求
    private int memoryNeed;
    private int allocatedMemory;

    // 分析和统计数据
    private long arrivalTime;
    private long startTime;
    private long finishTime;

    // 进程间通信（模拟邮箱/消息队列）
    private List<String> messages = new ArrayList<>();

    public PCB(int totalTime, int priority, int needA, int needB, int needC, int memoryNeed) {
        this.pid = pidSeq++;
        this.state = CREATED;
        this.totalTime = totalTime;
        this.runningTime = 0;
        this.priority = priority;
        this.needA = needA;
        this.needB = needB;
        this.needC = needC;
        this.getA = 0;
        this.getB = 0;
        this.getC = 0;
        this.memoryNeed = memoryNeed;
        this.allocatedMemory = 0;
        this.arrivalTime = System.currentTimeMillis();
    }
}