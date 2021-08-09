package transformers;

import com.unn.common.transformers.Transformer;
import com.unn.maestro.tgen.BrainfuckInterpreter;
import com.unn.maestro.tgen.ProgramGenerator;
import com.unn.maestro.transformers.TransformerCompiler;
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
