package dk.via.pro3.springiocexample;

import org.junit.jupiter.api.Test;
import dk.via.pro3.springiocexample.model.Drink;
import dk.via.pro3.springiocexample.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {"room.id=42", "room.name=Classroom"})
@ExtendWith(OutputCaptureExtension.class)
class SpringIocExampleApplicationTests {

    @Autowired private Drink drink;
    @Autowired private Room room;

    @Test
    void configuredRoomIsInjectedAndUsed(CapturedOutput output) {
        assertThat(room.getId()).isEqualTo(42);
        assertThat(room.getName()).isEqualTo("Classroom");
        drink.confirmObjectCreation();
        assertThat(output.getOut()).contains("Drink Object created and injected successfully",
                "Delivered to room 42 (Classroom)");
    }

}
