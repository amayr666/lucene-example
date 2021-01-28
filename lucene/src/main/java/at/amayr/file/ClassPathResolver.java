package at.amayr.file;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ClassPathResolver {
    public static Path ofClassPath(String dirName) throws URISyntaxException {
        var classLoader = ClassPathResolver.class.getClassLoader();
        return Paths.get(classLoader.getResource(dirName).toURI());
    }
}
