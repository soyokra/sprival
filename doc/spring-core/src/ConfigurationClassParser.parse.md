

```java

    public void parse(Set<BeanDefinitionHolder> configCandidates) {
		for (BeanDefinitionHolder holder : configCandidates) {
			BeanDefinition bd = holder.getBeanDefinition();
			try {
				if (bd instanceof AnnotatedBeanDefinition) {
					parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
				}
				else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
					parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
				}
				else {
					parse(bd.getBeanClassName(), holder.getBeanName());
				}
			}
			catch (BeanDefinitionStoreException ex) {
				throw ex;
			}
			catch (Throwable ex) {
				throw new BeanDefinitionStoreException(
						"Failed to parse configuration class [" + bd.getBeanClassName() + "]", ex);
			}
		}

		this.deferredImportSelectorHandler.process();
	}


    private class DeferredImportSelectorHandler {

        @Nullable
        private List<DeferredImportSelectorHolder> deferredImportSelectors = new ArrayList<>();

        /**
         * Handle the specified {@link DeferredImportSelector}. If deferred import
         * selectors are being collected, this registers this instance to the list. If
         * they are being processed, the {@link DeferredImportSelector} is also processed
         * immediately according to its {@link DeferredImportSelector.Group}.
         * @param configClass the source configuration class
         * @param importSelector the selector to handle
         */
        public void handle(ConfigurationClass configClass, DeferredImportSelector importSelector) {
            DeferredImportSelectorHolder holder = new DeferredImportSelectorHolder(configClass, importSelector);
            if (this.deferredImportSelectors == null) {
                DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
                handler.register(holder);
                handler.processGroupImports();
            }
            else {
                this.deferredImportSelectors.add(holder);
            }
        }

        public void process() {
            List<DeferredImportSelectorHolder> deferredImports = this.deferredImportSelectors;
            this.deferredImportSelectors = null;
            try {
                if (deferredImports != null) {
                    DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
                    deferredImports.sort(DEFERRED_IMPORT_COMPARATOR);
                    deferredImports.forEach(handler::register);
                    handler.processGroupImports();
                }
            }
            finally {
                this.deferredImportSelectors = new ArrayList<>();
            }
        }
    }
    
```