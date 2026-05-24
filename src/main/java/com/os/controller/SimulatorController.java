package com.os.controller;

import com.os.model.PCB;
import com.os.service.Dispatcher;
import com.os.service.MemoryManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SimulatorController {

    @Autowired
    private Dispatcher dispatcher;

    @Autowired
    private MemoryManager memoryManager;

    @PostMapping("/process")
    public Map<String, Object> createProcess(@RequestBody PCB request) {
        PCB pcb = new PCB(request.getTotalTime(), request.getPriority(), 
                          request.getNeedA(), request.getNeedB(), request.getNeedC(), 
                          request.getMemoryNeed());
        
        boolean success = dispatcher.addProcess(pcb);
        
        Map<String, Object> res = new HashMap<>();
        res.put("success", success);
        res.put("message", success ? "创建成功" : "空闲内存不足，分配失败！");
        return res;
    }

    @GetMapping("/state")
    public Map<String, Object> getState() {
        Map<String, Object> state = new HashMap<>();
        state.put("readyQueue", dispatcher.getStatusList());
        state.put("memoryBlocks", memoryManager.getPartitions());
        state.put("currentStrategy", dispatcher.getCurrentStrategyName());
        return state;
    }

    // 切换算法接口
    @PostMapping("/strategy")
    public Map<String, Object> changeStrategy(@RequestParam String algo) {
        dispatcher.changeStrategy(algo);
        Map<String, Object> res = new HashMap<>();
        res.put("success", true);
        return res;
    }

    // 增加撤销接口
    @DeleteMapping("/process/{pid}")
    public Map<String, Object> killProcess(@PathVariable int pid) {
        boolean success = dispatcher.killProcess(pid);
        Map<String, Object> res = new HashMap<>();
        res.put("success", success);
        return res;
    }

    // 增加通信接口
    @PostMapping("/message")
    public Map<String, Object> sendMessage(@RequestParam int toPid, @RequestParam String message) {
        boolean success = dispatcher.sendMessage(toPid, message);
        Map<String, Object> res = new HashMap<>();
        res.put("success", success);
        res.put("message", success ? "发送成功" : "找不到目标进程");
        return res;
    }
}