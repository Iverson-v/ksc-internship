package com.ksyun.train.plugins;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


//目标名统一为bootJar
@Mojo(name="bootJar")
public class BootJarMojo extends AbstractMojo {

    //默认是本地仓库的地址
    //在本地就是D:\MyIDE\maven\apache-maven-3.9.3\repository
    @Parameter(
            defaultValue="${settings.localRepository}",
            required=true
    )
    private String localRepository;


    //mvn clean package -D main.class=com.ksyun.train.App，会把com.ksyun.train.App传给mainClass。
    //在这里mainClass就是com.ksyun.train.App
    @Parameter(
            property = "main.class",
            required = true
    )
    private String mainClass;


    // 使用此对象获取 maven 项目相关信息
    @Component
    protected MavenProject project;
    public static final String TARGET_DIR = "target";
    public static final String CLASSES_DIR = "classes";
    public static final String JAR_SUFFIX = ".jar";
    public static final String ZIP_SUFFIX = ".zip";
    public static final String SEPARATOR = "/";
    public static final String LIB_DIR = "lib"+SEPARATOR;

    public static final String CONNECTOR="-";

    // 核心逻辑方法，将将项目打成的 jar 包以及依赖的 jar 包一起打成 .zip 包
    @Override
    public void execute() throws MojoFailureException {
        getLog().info("------------------------------begin------------------------------");
        getLog().info("mianClass is "+mainClass);
        getLog().info("project localRepository is " + localRepository);

        //baseDir=E:\MyData\ksc-data\maven-work\\use-plugin-demo
        File baseDir = project.getBasedir();
        getLog().info("project basedir is " + baseDir);

        //targetDir=E:\MyData\ksc-data\maven-work\\use-plugin-demo\target
        File targetDir = new File(baseDir, TARGET_DIR);

        //classDir=E:\MyData\ksc-data\maven-work\\use-plugin-demo\target\classes
        File classesDir = new File(targetDir, CLASSES_DIR);
        getLog().info("project classesDir is " + classesDir);

        // 获取项目依赖的 jar 包
        List<File> dependencyFiles = project.getDependencyArtifacts()
                .stream()
                .map(Artifact::getFile)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        //查看一下依赖的jar包
        for (File dependencyFile:dependencyFiles) {
            getLog().info("The jar package that the project depends on : " + dependencyFile.getAbsolutePath());
            //jars: D:\MyIDE\maven\apache-maven-3.9.3\repository\com\google\guava\guava\18.0\guava-18.0.jar
            //jars: D:\MyIDE\maven\apache-maven-3.9.3\repository\commons-collections\commons-collections\3.1\commons-collections-3.1.jar
        }

        //artifactName is use-plugin-demo-1.0
        String artifactName = project.getArtifactId() + CONNECTOR + project.getVersion();
        getLog().info("artifactName is " + artifactName);

        //Version=1.0
        getLog().info("Version is " + project.getVersion());



        // 第一步：  生成 manifest 文件
        String classPath = "";
        if (dependencyFiles.size() > 0) {
            classPath = dependencyFiles
                    .stream()
                    .map(file -> LIB_DIR + file.getName())
                    .collect(Collectors.joining(" "));
        }
        //classpath is   lib/guava-18.0.jar lib/commons-collections-3.1.jar
        getLog().info("classpath is " + classPath);
        String compilerSource = (String) project.getProperties().get("maven.compiler.source");
        Manifest manifest = generateManifest(mainClass, classPath,compilerSource);


        //第二步：   处理maven自带package产生的jar包。可以进行改名，以免覆盖。
        //bootJar = E:\MyData\ksc-data\maven-work\\use-plugin-demo\target 拼接 use-plugin-demo-1.0.jar
        File bootJar = new File(targetDir, artifactName +JAR_SUFFIX);
        //判断是否已经存在use-plugin-demo-1.0.jar，结果为true，说明maven打的默认包已经存在了。
        getLog().info("Does Maven's default package exist?  : " + bootJar.exists());
        //把maven自带package生命周期打的jar包重命名
        renameJarFile(bootJar,targetDir,artifactName +JAR_SUFFIX);


        // 第三步：    打jar包  把target/class目录下字节码文件以及生成MANIFEST.MF文件进行打包
        packJar(bootJar, manifest, classesDir);
        //bootjar=E:\MyData\ksc-data\maven-work\\use-plugin-demo\target\\use-plugin-demo-1.0.jar
        getLog().info("Absolute path of jar package: " + bootJar.getAbsolutePath());


        //第四步：     打zip压缩包，把jar包和两个依赖包guava、collections进行压缩。
        //bootZip: E:\MyData\ksc-data\maven-work\\use-plugin-demo\target\\use-plugin-demo-1.0.zip
        File bootZip = new File(targetDir, artifactName + ZIP_SUFFIX);
        //把jar包和依赖的jar包一起压缩。
        packZip(bootZip, bootJar,dependencyFiles);
        getLog().info("Absolute path of zip package: " + bootZip.getAbsolutePath());


        //第五步： 删除自己打在target目录下的jar包，因为已经压缩在zip里面了。
        clearJarFile(bootJar);
        getLog().info("------------------------------sucess------------------------------");
    }





    //生成Manifest对象。传入两个值分别是com.ksyun.train.App和lib/guava-18.0.jar lib/commons-collections-3.1.jar
    private Manifest generateManifest(String mainClass, String classPath,String compilerSource) {
        //maven默认的package插件生成的jar包manifest包含如下信息
        //Manifest-Version: 1.0
        //Build-Jdk-Spec: 1.8
        //Created-By: Maven JAR Plugin 3.3.0

        //必须设置Manifest-Version，Main-Class，和Class-path。
        Manifest manifest = new Manifest();
        //经过测试发现这个"Manifest-Version"属性必须赋值，不然使用java -jar会报错：use-plugin-demo-1.0.jar中没有主清单属性
        manifest.getMainAttributes().putValue("Manifest-Version", project.getVersion());

        //设置Main-Class，定义jar文件的入口类，该类必须是一个可执行的类，一旦定义了该属性即可通过 java -jar x.jar来运行该jar文件。
        //没有这一行也会报错  ：use-plugin-demo-1.0.jar中没有主清单属性
        manifest.getMainAttributes().putValue("Main-Class", mainClass);

        //传入对象不为空且calssPath长度大于0才设置属性值。
        if (Objects.nonNull(classPath) && classPath.length() > 0) {
            //Class-path，应用程序或者类装载器使用该值来构建内部的类搜索路径，没有这一行会找不到依赖。
            //没有这一行报错：Exception in thread "main" java.lang.NoClassDefFoundError
            manifest.getMainAttributes().putValue("Class-path", classPath);
        }

        //这是可选项，没有以下配置通过java -jar也能跑通程序。
        manifest.getMainAttributes().putValue("Build-Jdk-Spec", compilerSource);
        manifest.getMainAttributes().putValue("Created-By", "shihaoshi-2023-7-14");

        //这里自定义了MANIFEST文件。测试格式如下
        //Manifest-Version: 1.0
        //Class-path: lib/guava-18.0.jar lib/commons-collections-3.1.jar
        //Build-Jdk-Spec: 1.8
        //Created-By: shihaoshi-2023-7-14
        //Main-Class: com.ksyun.train.App

        return manifest;
    }



    //打jar包  把target/class目录下字节码文件以及生成MANIFEST.MF文件进行打包
    private void packJar(File bootJar, Manifest manifest, File classesDir) throws MojoFailureException {
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(bootJar.toPath()), manifest)) {
            //每层遍历打包
            addJarEntry(jos, classesDir, "");
            getLog().info("jar and dependencyFiles are packed into zip");

            jos.close();
            jos.flush();
        } catch (Exception e) {
            getLog().error("packing jar error!", e);
            throw new MojoFailureException("packing jar error!");
        }
    }

    //递归遍历每个目录，碰到不是目录的文件就进行打包。
    private void addJarEntry(JarOutputStream jos, File file, String rootPath) throws Exception {
        try {
            //当前file是否是目录，如果不是直接执行else把文件写到jar包中。
            if (file.isDirectory()) {

                File[] files = file.listFiles();
                //如果files为空就不用继续打包了，直接返回。
                if (Objects.isNull(files) || files.length == 0) {
                    return;
                }

                //第一次运行这里rootPath为空跳过，之后每次添加一个分隔符
                if (Objects.nonNull(rootPath) && rootPath.length() > 0) {
                    //这里必须为SEPARRAOR，不然会造成不同系统分隔符不同。
                    rootPath += SEPARATOR;
                }

                //递归遍历每层目录和文件
                for (File f : files) {
                    addJarEntry(jos, f, rootPath + f.getName());
                }
            } else {
                jos.putNextEntry(new JarEntry(rootPath));
                //把所有文件.class文件写到jar包中
                writeToFile(jos, file);
            }
        } catch (Exception e) {
            getLog().error("add jar entry error!", e);
            throw e;
        }
    }



    //打zip压缩包，把jar包和两个依赖包guava、collections进行压缩。
    private void packZip(File bootZip,  File bootJar,List<File> dependencyFiles) throws MojoFailureException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(bootZip.toPath()))) {
            addZipEntry(zos, bootZip, bootJar,dependencyFiles);
            getLog().info("jar and dependencyFiles are packed into zip");

            zos.close();
            zos.flush();
        } catch (Exception e) {
            getLog().error("packing zip error!", e);
            throw new MojoFailureException("packing zip error!");
        }
    }

    private void addZipEntry(ZipOutputStream zos, File bootZip,File bootJar, List<File> dependencyFiles) throws Exception {
        try {

            //jar包压缩
            zos.putNextEntry(new ZipEntry(bootJar.getName()));
            writeToFile(zos, bootJar);

            //判断是否有依赖
            if (Objects.nonNull(dependencyFiles) && dependencyFiles.size() > 0) {

                //循环遍历依赖
                for (File dependencyFile : dependencyFiles) {
                    //依赖包放在zip包的lib目录下进行压缩
                    zos.putNextEntry(new ZipEntry(LIB_DIR + dependencyFile.getName()));
                    writeToFile(zos, dependencyFile);
                }
            }
        } catch (Exception e) {
            getLog().error("add zip entry error!", e);
            throw e;
        }
    }



    private void writeToFile(OutputStream os, File file) throws Exception {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buff = new byte[1024];
            int len;
            while ((len = fis.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
        } catch (Exception e) {
            getLog().error("write file error", e);
            throw e;
        }
    }

    //我绑定的是package生命周期，maven自带package会先打成jar包，所以这里把maven自带package生命周期打的jar包重命名。
    private void renameJarFile(File JarFile,File targetDir,String fileName){
        try {
            boolean result = JarFile.renameTo(new File(targetDir, "maven's-default-package"+ CONNECTOR + fileName));
            getLog().info("rename jar: " + result);
        } catch (Exception e) {
            getLog().error("rename jar error!");
            throw e;
        }
    }


    //删除自己打在target目录下的jar包
    private void clearJarFile(File file) {
        try {
            boolean result = file.delete();
            getLog().info("delete jar : " + result);
        } catch (Exception e) {
            getLog().error("delete jar error!");
            throw e;
        }
    }

}
