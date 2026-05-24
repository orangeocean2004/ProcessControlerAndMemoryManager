package com.os.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MemoryBlock {
    private int startAddress;
    private int length;
    // 如果为 null 或空字符串则代表未分配，否则记录 PID
    private String status; 
}