package transformers;

import com.unn.maestro.transformers.turing.BrainfuckInterpreter;
import com.unn.maestro.transformers.turing.ProgramGenerator;
import org.junit.Test;

public class TestProgramGenerator {
    @Test
    public void testProgramGenerator() {
        ProgramGenerator generator = new ProgramGenerator();
        String program = generator.next();
        System.out.println(program);
        BrainfuckInterpreter.interpret(program);
    }
}
