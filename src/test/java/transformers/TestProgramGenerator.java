package transformers;

import com.unn.common.boosting.BrainfuckInterpreter;
import com.unn.maestro.transformers.turing.EntropyGenerator;
import com.unn.maestro.transformers.turing.ProgramGenerator;
import org.junit.Test;

public class TestProgramGenerator {
    @Test
    public void testProgramGenerator() {
        ProgramGenerator generator = new ProgramGenerator();
        String program = generator.next();
        System.out.println(program);
        new BrainfuckInterpreter().interpret(program);
    }

    @Test
    public void testEntropyGenerator() {
        EntropyGenerator generator = new EntropyGenerator();
        generator.run();
    }
}
