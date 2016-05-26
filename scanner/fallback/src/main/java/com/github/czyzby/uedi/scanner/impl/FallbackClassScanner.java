package com.github.czyzby.uedi.scanner.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import com.github.czyzby.uedi.scanner.ClassScanner;

/** Uses reflection to analyze current classpath.
 *
 * @author MJ
 * @see FixedClassScanner */
public class FallbackClassScanner implements ClassScanner {
    private static final String CLASS_FILE_EXTENSION = ".class";
    private static final String JAR_FILE_EXTENSION = ".jar";

    @Override
    public Iterable<Class<?>> getClassesImplementing(final Class<?> root, final Class<?>... interfaces) {
        final String mainPackageName = root.getPackage().getName();
        final String classPathRoot = getClassPathRoot(mainPackageName);
        final ClassLoader classLoader = root.getClassLoader() == null ? ClassLoader.getSystemClassLoader()
                : root.getClassLoader();
        try {
            final Enumeration<URL> resources = classLoader.getResources(classPathRoot);
            final Queue<DepthFile> filesWithDepthsToProcess = new LinkedList<DepthFile>();
            while (resources.hasMoreElements()) {
                try {
                    filesWithDepthsToProcess.add(new DepthFile(0, toFile(resources.nextElement())));
                } catch (final Exception uriSyntaxException) {
                    ignore(uriSyntaxException); // Will throw an exception for non-hierarchical files.
                }
            }
            if (filesWithDepthsToProcess.isEmpty()) {
                return extractFromJar(classPathRoot, classLoader, interfaces);
            }
            return extractFromBinaries(mainPackageName, filesWithDepthsToProcess, interfaces);
        } catch (final Exception exception) {
            throw new RuntimeException("Unable to scan classpath.", exception);
        }
    }

    /** Override to inspect ignored exceptions.
     *
     * @param expectedException was thrown. */
    protected void ignore(final Exception expectedException) {
    }

    private static Set<Class<?>> extractFromBinaries(final String mainPackageName,
            final Queue<DepthFile> filesWithDepthsToProcess, final Class<?>... interfaces) throws Exception {
        final Set<Class<?>> result = new HashSet<Class<?>>();
        while (!filesWithDepthsToProcess.isEmpty()) {
            final DepthFile classPathFileWithDepth = filesWithDepthsToProcess.poll();
            final File classPathFile = classPathFileWithDepth.file;
            final int depth = classPathFileWithDepth.depth;
            if (classPathFile.isDirectory()) {
                addAllChildren(filesWithDepthsToProcess, classPathFile, depth);
            } else {
                final String className = getBinaryClassName(mainPackageName, classPathFile, depth);
                if (!isFromPackage(mainPackageName, className)) {
                    continue;
                }
                final Class<?> classToProcess = Class.forName(className);
                processClass(result, classToProcess, interfaces);
            }
        }
        return result;
    }

    private static boolean isFromPackage(final String mainPackageName, final String className) {
        return className.indexOf('-') < 0 && className.startsWith(mainPackageName); // True if not package-info.
    }

    private static File toFile(final URL url) throws URISyntaxException {
        return new File(url.toURI()).getAbsoluteFile();
    }

    private static void addAllChildren(final Queue<DepthFile> rootFiles, final File classPathFile, int depth) {
        depth++;
        for (final File file : classPathFile.listFiles()) {
            if (file.isDirectory() || file.getName().endsWith(CLASS_FILE_EXTENSION)) {
                rootFiles.add(new DepthFile(depth, file));
            }
        }
    }

    private static String getBinaryClassName(final String mainPackageName, final File classPathFile, final int depth) {
        final String[] classFolders = classPathFile.getPath().split(File.separator);
        final StringBuilder builder = new StringBuilder(mainPackageName);
        for (int folderIndex = classFolders.length - depth; folderIndex < classFolders.length - 1; folderIndex++) {
            builder.append('.').append(classFolders[folderIndex]);
        }
        final String classFileName = classFolders[classFolders.length - 1];
        builder.append('.').append(classFileName.substring(0, classFileName.length() - CLASS_FILE_EXTENSION.length()));
        return builder.toString();
    }

    private static String getClassPathRoot(final String mainPackageName) {
        return mainPackageName.replace('.', File.separatorChar);
    }

    private static Set<Class<?>> extractFromJar(final String classPathRoot, final ClassLoader classLoader,
            final Class<?>... interfaces) throws Exception {
        final List<JarFile> filesToProcess = getJarFilesToProcess();
        final Set<Class<?>> result = new HashSet<Class<?>>();
        for (final JarFile jarFile : filesToProcess) {
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                processEntry(classPathRoot, result, entry, interfaces);
            }
        }
        return result;
    }

    private static List<JarFile> getJarFilesToProcess() throws URISyntaxException, IOException {
        final List<JarFile> filesToProcess = new ArrayList<JarFile>();
        final File jarDirectory = new File(ClassLoader.getSystemClassLoader().getResource(".").toURI());
        for (final File file : jarDirectory.listFiles()) {
            if (file.getName().endsWith(JAR_FILE_EXTENSION)) {
                filesToProcess.add(new JarFile(file));
            }
        }
        return filesToProcess;
    }

    private static void processEntry(final String classPathRoot, final Set<Class<?>> result, final JarEntry entry,
            final Class<?>... interfaces) throws Exception {
        if (!entry.isDirectory()) {
            final String entryName = entry.getName().replace('/', File.separatorChar);
            if (isFromPackage(classPathRoot, entryName) && entryName.endsWith(CLASS_FILE_EXTENSION)) {
                final String className = jarEntryToClassName(entryName);
                final Class<?> classToProcess = Class.forName(className);
                processClass(result, classToProcess, interfaces);
            }
        }
    }

    private static void processClass(final Set<Class<?>> result, final Class<?> classToProcess,
            final Class<?>... interfaces) {
        if (Modifier.isAbstract(classToProcess.getModifiers()) || classToProcess.isInterface()) {
            return;
        }
        for (final Class<?> possibleMatch : interfaces) {
            if (possibleMatch.isAssignableFrom(classToProcess)) {
                result.add(classToProcess);
                return;
            }
        }
    }

    private static String jarEntryToClassName(final String entryName) {
        return entryName.substring(0, entryName.length() - CLASS_FILE_EXTENSION.length()).replace(File.separatorChar,
                '.');
    }

    /** Utility container.
     *
     * @author MJ */
    private static class DepthFile {
        private final int depth;
        private final File file;

        public DepthFile(final int depth, final File file) {
            this.depth = depth;
            this.file = file;
        }
    }
}
