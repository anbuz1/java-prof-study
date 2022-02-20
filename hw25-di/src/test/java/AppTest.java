import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import ru.buz.appcontainer.AppComponentsContainerImpl;
import ru.buz.config.AppConfig;
import ru.buz.services.*;

import java.io.PrintStream;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {


    @DisplayName("Iз контекста тремя способами должен корректно доставаться компонент с проставленными полями")
    @ParameterizedTest(name = "Достаем по: {0}")
    @CsvSource(value = {"GameProcessor, ru.buz.services.GameProcessor",
            "GameProcessorImpl, ru.buz.services.GameProcessor",
            "gameProcessor, ru.buz.services.GameProcessor",

            "IOService, ru.buz.services.IOService",
            "IOServiceConsole, ru.buz.services.IOService",
            "ioService, ru.buz.services.IOService",

            "PlayerService, ru.buz.services.PlayerService",
            "PlayerServiceImpl, ru.buz.services.PlayerService",
            "playerService, ru.buz.services.PlayerService",

            "EquationPreparer, ru.buz.services.EquationPreparer",
            "EquationPreparerImpl, ru.buz.services.EquationPreparer",
            "equationPreparer, ru.buz.services.EquationPreparer"
    })
    public void shouldExtractFromContextCorrectComponentWithNotNullFields(String classNameOrBeanId, Class<?> rootClass) throws Exception {
        var ctx = new AppComponentsContainerImpl(AppConfig.class);

        assertThat(classNameOrBeanId).isNotEmpty();
        Object component;
        if (classNameOrBeanId.charAt(0) == classNameOrBeanId.toUpperCase().charAt(0)) {
            Class<?> gameProcessorClass = Class.forName("ru.buz.services." + classNameOrBeanId);
            assertThat(rootClass).isAssignableFrom(gameProcessorClass);

            component = ctx.getAppComponent(gameProcessorClass);
        } else {
            component = ctx.getAppComponent(classNameOrBeanId);
        }
        assertThat(component).isNotNull();
        assertThat(rootClass).isAssignableFrom(component.getClass());

        var fields = Arrays.stream(component.getClass().getDeclaredFields())
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .peek(f -> f.setAccessible(true))
                .collect(Collectors.toList());

        for (var field: fields){
            var fieldValue = field.get(component);
            assertThat(fieldValue).isNotNull().isInstanceOfAny(IOService.class, PlayerService.class,
                    EquationPreparer.class, PrintStream.class, Scanner.class);
        }

    }
}