package co.com.backend.reactive.config;


public class UseCasesConfigTest {

    // @Test
    // void testUseCaseBeansExist() {
    //     try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(TestConfig.class)) {
    //         String[] beanNames = context.getBeanDefinitionNames();

    //         boolean useCaseBeanFound = false;
    //         for (String beanName : beanNames) {
    //             if (beanName.endsWith("UseCase")) {
    //                 useCaseBeanFound = true;
    //                 break;
    //             }
    //         }

    //         assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
    //     }
    // }

    // @Configuration
    // @Import(UseCasesConfig.class)
    // static class TestConfig {

    //     @Bean
    //     public MyUseCase myUseCase() {
    //         return new MyUseCase();
    //     }
    // }

    // static class MyUseCase {
    //     public String execute() {
    //         return "MyUseCase Test";
    //     }
    // }
}