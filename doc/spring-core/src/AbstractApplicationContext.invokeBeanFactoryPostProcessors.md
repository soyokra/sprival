## 简述
调用BeanDefinition接口
BeanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry
BeanFactoryPostProcessor.postProcessBeanFactory

```java
final class PostProcessorRegistrationDelegate {


    public static void invokeBeanFactoryPostProcessors(
          ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

       // Invoke BeanDefinitionRegistryPostProcessors first, if any.
       Set<String> processedBeans = new HashSet<>();

       if (beanFactory instanceof BeanDefinitionRegistry) {
          BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
          List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
          List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

          for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
             if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
                BeanDefinitionRegistryPostProcessor registryProcessor =
                      (BeanDefinitionRegistryPostProcessor) postProcessor;
                      
                registryProcessor.postProcessBeanDefinitionRegistry(registry);
                registryProcessors.add(registryProcessor);
             }
             else {
                regularPostProcessors.add(postProcessor);
             }
          }

          // Do not initialize FactoryBeans here: We need to leave all regular beans
          // uninitialized to let the bean factory post-processors apply to them!
          // Separate between BeanDefinitionRegistryPostProcessors that implement
          // PriorityOrdered, Ordered, and the rest.
          List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

          // First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
          String[] postProcessorNames =
                beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
          for (String ppName : postProcessorNames) {
             if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
                currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                processedBeans.add(ppName);
             }
          }
          sortPostProcessors(currentRegistryProcessors, beanFactory);
          registryProcessors.addAll(currentRegistryProcessors);
          invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
          currentRegistryProcessors.clear();

          // Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
          postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
          for (String ppName : postProcessorNames) {
             if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
                currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                processedBeans.add(ppName);
             }
          }
          sortPostProcessors(currentRegistryProcessors, beanFactory);
          registryProcessors.addAll(currentRegistryProcessors);
          invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
          currentRegistryProcessors.clear();

          // Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
          boolean reiterate = true;
          while (reiterate) {
             reiterate = false;
             postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
             for (String ppName : postProcessorNames) {
                if (!processedBeans.contains(ppName)) {
                   currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
                   processedBeans.add(ppName);
                   reiterate = true;
                }
             }
             sortPostProcessors(currentRegistryProcessors, beanFactory);
             registryProcessors.addAll(currentRegistryProcessors);
             invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
             currentRegistryProcessors.clear();
          }

          // Now, invoke the postProcessBeanFactory callback of all processors handled so far.
          invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
          invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
       }

       else {
          // Invoke factory processors registered with the context instance.
          invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
       }

       // Do not initialize FactoryBeans here: We need to leave all regular beans
       // uninitialized to let the bean factory post-processors apply to them!
       String[] postProcessorNames =
             beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

       // Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
       // Ordered, and the rest.
       List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
       List<String> orderedPostProcessorNames = new ArrayList<>();
       List<String> nonOrderedPostProcessorNames = new ArrayList<>();
       for (String ppName : postProcessorNames) {
          if (processedBeans.contains(ppName)) {
             // skip - already processed in first phase above
          }
          else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
             priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
          }
          else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
             orderedPostProcessorNames.add(ppName);
          }
          else {
             nonOrderedPostProcessorNames.add(ppName);
          }
       }

       // First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
       sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
       invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

       // Next, invoke the BeanFactoryPostProcessors that implement Ordered.
       List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>(orderedPostProcessorNames.size());
       for (String postProcessorName : orderedPostProcessorNames) {
          orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
       }
       sortPostProcessors(orderedPostProcessors, beanFactory);
       invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

       // Finally, invoke all other BeanFactoryPostProcessors.
       List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>(nonOrderedPostProcessorNames.size());
       for (String postProcessorName : nonOrderedPostProcessorNames) {
          nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
       }
       invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

       // Clear cached merged bean definitions since the post-processors might have
       // modified the original metadata, e.g. replacing placeholders in values...
       beanFactory.clearMetadataCache();
    }
}
```

扫描文件，创建BeanDefinition
执行ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry，扫描包下文件注册beanDefinition
```java
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
            for (BeanDefinition candidate : candidates) {
                ScopeMetadata scopeMetadata = this.scopeMetadataResolver.resolveScopeMetadata(candidate);
                candidate.setScope(scopeMetadata.getScopeName());
                String beanName = this.beanNameGenerator.generateBeanName(candidate, this.registry);
                if (candidate instanceof AbstractBeanDefinition) {
                    postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
                }
                if (candidate instanceof AnnotatedBeanDefinition) {
                    AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
                }
                if (checkCandidate(beanName, candidate)) {
                    BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
                    definitionHolder =
                            AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, this.registry);
                    beanDefinitions.add(definitionHolder);
                    registerBeanDefinition(definitionHolder, this.registry);
                }
            }
        }
        return beanDefinitions;
    }
}
```

调用栈
```text
doScan:273, ClassPathBeanDefinitionScanner (org.springframework.context.annotation)
parse:132, ComponentScanAnnotationParser (org.springframework.context.annotation)
doProcessConfigurationClass:296, ConfigurationClassParser (org.springframework.context.annotation)
processConfigurationClass:250, ConfigurationClassParser (org.springframework.context.annotation)
parse:207, ConfigurationClassParser (org.springframework.context.annotation)
parse:175, ConfigurationClassParser (org.springframework.context.annotation)
processConfigBeanDefinitions:320, ConfigurationClassPostProcessor (org.springframework.context.annotation)
postProcessBeanDefinitionRegistry:237, ConfigurationClassPostProcessor (org.springframework.context.annotation)
invokeBeanDefinitionRegistryPostProcessors:280, PostProcessorRegistrationDelegate (org.springframework.context.support)
invokeBeanFactoryPostProcessors:96, PostProcessorRegistrationDelegate (org.springframework.context.support)
invokeBeanFactoryPostProcessors:707, AbstractApplicationContext (org.springframework.context.support)
refresh:533, AbstractApplicationContext (org.springframework.context.support)
refresh:143, ServletWebServerApplicationContext (org.springframework.boot.web.servlet.context)
refresh:755, SpringApplication (org.springframework.boot)
refresh:747, SpringApplication (org.springframework.boot)
refreshContext:402, SpringApplication (org.springframework.boot)
run:312, SpringApplication (org.springframework.boot)
main:35, StartApplication (com.zuzuche.widget.start)
```