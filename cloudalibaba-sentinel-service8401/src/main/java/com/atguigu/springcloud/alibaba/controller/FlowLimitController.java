package com.atguigu.springcloud.alibaba.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @auther zzyy
 * @create 2020-02-24 16:26
 */
@RestController
@Slf4j
public class FlowLimitController
{
    /**
     * 流控模式--qps测试
     * @return
     */
    @GetMapping("/testA")
    public String testA()
    {
        return "------testA";
    }

    /**
     * 流控模式--线程数测试    流控模式--关联测试。testA，B满了A也不能访问了
     * warm up  预热/冷启动----让流量缓缓增加到达阈值，避免系统冲垮
     * 排队等待  ---》排队等待 一秒一个，多余等待，设置超时时间
     * 链路
     * @return
     */
    @GetMapping("/testB")
    public String testB()
    {
        log.info(Thread.currentThread().getName()+"\t"+"...testB");
        return "------testB";
    }

    /**
     * 降级 RT（平均响应时间 (DEGRADE_GRADE_RT)）
     * 资源的平均响应时间超过阈值（DegradeRule 中的 count，以 ms 为单位）之后，资源进入准降级状态。接下来如果持续进入 5 个请求，
     * 它们的 RT 都持续超过这个阈值，那么在接下的时间窗口（DegradeRule 中的 timeWindow，以 s 为单位）之内，
     * 对这个方法的调用都会自动地返回（抛出 DegradeException）。在下一个时间窗口到来时, 会接着再放入5个请求, 再重复上面的判断.
     *  异常比例
     *  异常数
     * 熔断降级 和hystrix熔断相似，但没有半开状态
     * Sentinel除了流量控制以外，对调用链路中不稳定的资源进行熔断降级也是保障高可用的重要措施之一。
     * 由于调用关系的复杂性，如果调用链路中的某个资源不稳定，最终会导致请求发生堆积。
     * Sentinel 熔断降级会在调用链路中某个资源出现不稳定状态时（例如调用超时或异常比例升高），
     * 对这个资源的调用进行限制，让请求快速失败，避免影响到其它的资源而导致级联错误。
     * 当资源被降级后，在接下来的降级时间窗口之内，对该资源的调用都自动熔断（默认行为是抛出 DegradeException）。
     * @return
     */
    @GetMapping("/testD")
    public String testD()
    {
        try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
        log.info("testD 测试RT");

//        log.info("testD 异常比例");
//        int age = 10/0;
        return "------testD";
    }

    @GetMapping("/testE")
    public String testE()
    {
        log.info("testE 测试异常数");
        int age = 10/0;
        return "------testE 测试异常数";
    }

    /**
     * 热点key      比如频繁访问的一个，带p1这个参数的进行限流   降级
     * @param p1
     * @param p2
     * @return
     */
    @GetMapping("/testHotKey")
    @SentinelResource(value = "testHotKey",blockHandler = "deal_testHotKey")
    public String testHotKey(@RequestParam(value = "p1",required = false) String p1,
                             @RequestParam(value = "p2",required = false) String p2)
    {
        //int age = 10/0;
        return "------testHotKey";
    }

    /**
     * 降级兜底方法
     * @param p1
     * @param p2
     * @param exception
     * @return
     */
    public String deal_testHotKey (String p1, String p2, BlockException exception)
    {
        return "------deal_testHotKey,o(╥﹏╥)o";  //sentinel系统默认的提示：Blocked by Sentinel (flow limiting)
    }

}
