package transformers;

import com.unn.common.transformers.Transformer;
import com.unn.maestro.transformers.TransformerCompiler;
import org.junit.Test;

public class TestJavaCompiler {

    @Test
    public void testJavaCompiler() {
        Transformer t = TransformerCompiler.process("import com.unn.common.dataset.DatasetDescriptor;\n" +
                "import com.unn.common.dataset.Row;\n" +
                "import com.unn.common.transformers.RuntimeContext;\n" +
                "import com.unn.common.transformers.Transformer;\n" +
                "import com.unn.common.transformers.TransformerRuntime;\n" +
                "import javafx.util.Pair;\n" +
                "import java.util.List;\n" +
                "\n" +
                "public class Dummy extends Transformer {\n" +
                "    @Override\n" +
                "    public void setRuntime(TransformerRuntime runtime) {\n" +
                "        System.out.println(\"Testing Dummy compiler\");\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public RuntimeContext init(List<DatasetDescriptor> namespaces) {\n" +
                "        return null;\n" +
                "    }\n" +
                "\n" +
                "    @Override\n" +
                "    public Pair<Integer, Row> process(RuntimeContext context, String tNamespace, int primer) {\n" +
                "        return null;\n" +
                "    }\n" +
                "}\n");
        t.setRuntime(null);
    }
}
