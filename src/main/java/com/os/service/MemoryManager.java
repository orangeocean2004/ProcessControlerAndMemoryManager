package com.os.service;

import com.os.model.MemoryBlock;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemoryManager {
    private List<MemoryBlock> partitions = new ArrayList<>();
    private final int TOTAL_MEMORY = 1024; // 模拟 1024KB 内存

    public MemoryManager() {
        // 初始化时，整个内存是一个完整的空闲块
        partitions.add(new MemoryBlock(0, TOTAL_MEMORY, "unallocated"));
    }

    // 首次适应算法 (First Fit)
    public synchronized boolean allocate(int pid, int size) {
        for (int i = 0; i < partitions.size(); i++) {
            MemoryBlock block = partitions.get(i);
            // 找到第一个满足大小的空闲块
            if ("unallocated".equals(block.getStatus()) && block.getLength() >= size) {
                if (block.getLength() == size) {
                    // 大小刚好，直接占用
                    block.setStatus(String.valueOf(pid));
                } else {
                    // 大小有余，切分出新块
                    MemoryBlock newBlock = new MemoryBlock(block.getStartAddress(), size, String.valueOf(pid));
                    block.setStartAddress(block.getStartAddress() + size);
                    block.setLength(block.getLength() - size);
                    partitions.add(i, newBlock);
                }
                return true;
            }
        }
        return false; // 内存不足
    }

    // 内存回收与碎片合并
    public synchronized void deallocate(int pid) {
        // 释放归属该进程的所有内存块
        for (MemoryBlock block : partitions) {
            if (String.valueOf(pid).equals(block.getStatus())) {
                block.setStatus("unallocated");
            }
        }
        mergeFreeBlocks();
    }

    // 合并相邻的空闲碎片
    private void mergeFreeBlocks() {
        for (int i = 0; i < partitions.size() - 1; i++) {
            MemoryBlock current = partitions.get(i);
            MemoryBlock next = partitions.get(i + 1);
            if ("unallocated".equals(current.getStatus()) && "unallocated".equals(next.getStatus())) {
                current.setLength(current.getLength() + next.getLength());
                partitions.remove(i + 1);
                i--; // 回退一步，继续检查合并后的块与下一个块
            }
        }
    }

    // 获取当前内存分布表
    public synchronized List<MemoryBlock> getPartitions() {
        return new ArrayList<>(partitions);
    }
}