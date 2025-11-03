package com.soyokra.sprival.loadtest.standalone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soyokra.sprival.app.http.request.OrderInsertRequest;
import com.soyokra.sprival.fixture.TestDataBuilder;
import com.soyokra.sprival.loadtest.standalone.scenario.*;
import com.soyokra.sprival.util.HttpLoadTestExecutor;
import com.soyokra.sprival.util.LoadTestResult;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 独立压力测试工具启动类
 * 
 * <p>支持应用外部执行的压力测试工具，提供多种流量场景</p>
 * 
 * <p>使用方法：</p>
 * <pre>{@code
 * # 稳定流量测试
 * mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
 *   -Dexec.args="--url http://localhost:8338/api/order/insert --scenario steady --threads 10 --duration 60"
 * 
 * # 突发流量测试
 * mvn exec:java -Dexec.mainClass="com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester" \
 *   -Dexec.args="--url http://localhost:8338/api/order/insert --scenario burst --peak-threads 100"
 * }</pre>
 * 
 * @author sprival-test-framework
 * @since 2025-11-03
 */
public class StandaloneLoadTester {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 主方法
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        try {
            // 解析命令行参数
            Map<String, String> params = parseArguments(args);
            
            // 验证必填参数
            validateRequiredParams(params);
            
            // 创建HTTP客户端
            CloseableHttpClient httpClient = createHttpClient();
            
            // 创建测试上下文
            LoadTestContext context = createContext(params, httpClient);
            
            // 选择并执行测试场景
            LoadTestScenario scenario = selectScenario(params);
            
            System.out.println("========================================");
            System.out.println("独立压力测试工具");
            System.out.println("========================================");
            System.out.println("目标URL: " + context.getUrl());
            System.out.println("测试场景: " + scenario.getName());
            System.out.println("场景描述: " + scenario.getDescription());
            System.out.println("========================================\n");
            
            // 执行测试
            LoadTestResult result = scenario.execute(context);
            
            // 输出测试报告
            result.printReport();
            
            // 导出报告（如果指定）
            if (context.getReportFile() != null) {
                exportReport(result, context.getReportFile());
            }
            
            // 清理资源
            httpClient.close();
            
        } catch (Exception e) {
            System.err.println("测试执行失败: " + e.getMessage());
            e.printStackTrace();
            printUsage();
            System.exit(1);
        }
    }
    
    /**
     * 解析命令行参数
     * 
     * @param args 命令行参数数组
     * @return 参数Map
     */
    private static Map<String, String> parseArguments(String[] args) {
        Map<String, String> params = new HashMap<>();
        
        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    params.put(key, args[i + 1]);
                    i++;
                } else {
                    params.put(key, "true");
                }
            }
        }
        
        return params;
    }
    
    /**
     * 验证必填参数
     * 
     * @param params 参数Map
     * @throws IllegalArgumentException 参数缺失
     */
    private static void validateRequiredParams(Map<String, String> params) {
        if (!params.containsKey("url")) {
            throw new IllegalArgumentException("缺少必填参数: --url");
        }
        if (!params.containsKey("scenario")) {
            throw new IllegalArgumentException("缺少必填参数: --scenario");
        }
    }
    
    /**
     * 创建HTTP客户端
     * 
     * @return HTTP客户端
     */
    private static CloseableHttpClient createHttpClient() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(200);
        connectionManager.setDefaultMaxPerRoute(50);
        
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }
    
    /**
     * 创建测试上下文
     * 
     * @param params 参数Map
     * @param httpClient HTTP客户端
     * @return 测试上下文
     */
    private static LoadTestContext createContext(Map<String, String> params, CloseableHttpClient httpClient) {
        HttpLoadTestExecutor executor = new HttpLoadTestExecutor(httpClient);
        
        return LoadTestContext.builder()
                .url(params.get("url"))
                .httpClient(httpClient)
                .executor(executor)
                .requestBodySupplier(() -> createOrderRequest())
                .defaultThreads(Integer.parseInt(params.getOrDefault("threads", "10")))
                .defaultDuration(Integer.parseInt(params.getOrDefault("duration", "60")))
                .defaultWarmup(Integer.parseInt(params.getOrDefault("warmup", "10")))
                .reportInterval(Integer.parseInt(params.getOrDefault("interval", "10")))
                .reportFile(params.get("report-file"))
                .build();
    }
    
    /**
     * 选择测试场景
     * 
     * @param params 参数Map
     * @return 测试场景
     */
    private static LoadTestScenario selectScenario(Map<String, String> params) {
        String scenarioName = params.get("scenario").toLowerCase();
        
        switch (scenarioName) {
            case "steady":
                return new SteadyTrafficScenario(
                        Integer.parseInt(params.getOrDefault("threads", "10")),
                        Integer.parseInt(params.getOrDefault("duration", "60")),
                        Integer.parseInt(params.getOrDefault("warmup", "10"))
                );
                
            case "burst":
                return new BurstTrafficScenario(
                        Integer.parseInt(params.getOrDefault("peak-threads", "100")),
                        Integer.parseInt(params.getOrDefault("burst-duration", "30")),
                        Integer.parseInt(params.getOrDefault("ramp-up-time", "5"))
                );
                
            case "rampup":
                return new RampUpTrafficScenario(
                        Integer.parseInt(params.getOrDefault("start-threads", "1")),
                        Integer.parseInt(params.getOrDefault("max-threads", "100")),
                        Integer.parseInt(params.getOrDefault("step", "10")),
                        Integer.parseInt(params.getOrDefault("step-duration", "30"))
                );
                
            case "spike":
                return new SpikeTrafficScenario(
                        Integer.parseInt(params.getOrDefault("high-threads", "50")),
                        Integer.parseInt(params.getOrDefault("low-threads", "5")),
                        Integer.parseInt(params.getOrDefault("spike-duration", "20")),
                        Integer.parseInt(params.getOrDefault("cycles", "5"))
                );
                
            case "endurance":
                return new EnduranceTrafficScenario(
                        Integer.parseInt(params.getOrDefault("threads", "10")),
                        Integer.parseInt(params.getOrDefault("duration", "3600"))
                );
                
            default:
                throw new IllegalArgumentException("未知的测试场景: " + scenarioName);
        }
    }
    
    /**
     * 创建订单请求
     * 
     * @return 订单请求JSON字符串
     */
    private static String createOrderRequest() {
        try {
            OrderInsertRequest request = new OrderInsertRequest();
            request.setOrderId(TestDataBuilder.generateId("ORDER"));
            request.setTradeId(TestDataBuilder.generateId("TRADE"));
            request.setParentOrderId(null);
            request.setOrderType("NORMAL");
            request.setUserId(TestDataBuilder.generateId("USER"));
            request.setPartnerId(TestDataBuilder.generateId("PARTNER"));
            request.setSupplierId(TestDataBuilder.generateId("SUPPLIER"));
            request.setIdempotentId(TestDataBuilder.generateUUID("IDEM"));
            request.setStatusNo(1);
            request.setBusinessStatus(0);
            
            return objectMapper.writeValueAsString(request);
        } catch (Exception e) {
            throw new RuntimeException("创建订单请求失败", e);
        }
    }
    
    /**
     * 导出测试报告
     * 
     * @param result 测试结果
     * @param filePath 文件路径
     */
    private static void exportReport(LoadTestResult result, String filePath) {
        try {
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            java.nio.file.Files.write(java.nio.file.Paths.get(filePath), json.getBytes());
            System.out.println("\n测试报告已导出到: " + filePath);
        } catch (Exception e) {
            System.err.println("导出报告失败: " + e.getMessage());
        }
    }
    
    /**
     * 打印使用说明
     */
    private static void printUsage() {
        System.out.println("\n使用说明:");
        System.out.println("========================================");
        System.out.println("必填参数:");
        System.out.println("  --url <URL>              目标URL");
        System.out.println("  --scenario <SCENARIO>    测试场景: steady/burst/rampup/spike/endurance");
        System.out.println("\n通用可选参数:");
        System.out.println("  --threads <NUM>          并发线程数 (默认: 10)");
        System.out.println("  --duration <SECONDS>     测试持续时间 (默认: 60)");
        System.out.println("  --warmup <SECONDS>       预热时间 (默认: 10)");
        System.out.println("  --report-file <FILE>     报告输出文件");
        System.out.println("  --interval <SECONDS>     实时报告间隔 (默认: 10)");
        System.out.println("\nBurst场景参数:");
        System.out.println("  --peak-threads <NUM>     峰值并发数 (默认: 100)");
        System.out.println("  --burst-duration <SEC>   突发持续时间 (默认: 30)");
        System.out.println("  --ramp-up-time <SEC>     达到峰值时间 (默认: 5)");
        System.out.println("\nRampUp场景参数:");
        System.out.println("  --start-threads <NUM>    起始并发数 (默认: 1)");
        System.out.println("  --max-threads <NUM>      最大并发数 (默认: 100)");
        System.out.println("  --step <NUM>             每次增加线程数 (默认: 10)");
        System.out.println("  --step-duration <SEC>    每阶段持续时间 (默认: 30)");
        System.out.println("\nSpike场景参数:");
        System.out.println("  --high-threads <NUM>     高并发数 (默认: 50)");
        System.out.println("  --low-threads <NUM>      低并发数 (默认: 5)");
        System.out.println("  --spike-duration <SEC>   脉冲持续时间 (默认: 20)");
        System.out.println("  --cycles <NUM>           脉冲次数 (默认: 5)");
        System.out.println("\n示例:");
        System.out.println("  mvn exec:java -Dexec.mainClass=\"com.soyokra.sprival.loadtest.standalone.StandaloneLoadTester\" \\");
        System.out.println("    -Dexec.args=\"--url http://localhost:8338/api/order/insert --scenario steady --threads 20 --duration 120\"");
        System.out.println("========================================");
    }
}

