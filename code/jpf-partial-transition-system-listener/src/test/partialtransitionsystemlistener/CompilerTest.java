package partialtransitionsystemlistener;

import static org.junit.Assert.assertNotNull;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Test;

public class CompilerTest {

    @Test
    public void test() {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compiler);
    }
}