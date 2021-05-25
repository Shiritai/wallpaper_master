# 瑣碎學習紀錄

## 執行 .jar

`java --module-path D:/ShiZu_Code/Java/javafx-sdk-15.0.1/lib --add-modules javafx.controls,javafx.fxml,javafx.media -jar wallpaper_master-1.0-SNAPSHOT.jar`
`java --module-path JAVAFX_PATH --add-modules JAVAFX_JARS -jar JAVAFX_PROJECT.jar`

## 注意引入新 Dependencies 時, 盯著 Git 看, 注意 target classes 是否被刪掉...

[VSCode 硬核引入 .jar 的方法](https://dzone.com/articles/simple-ways-to-add-and-work-with-a-jar-file-in-you)

## pom.xml `<build>...</build>` 舊標籤暫存

```xml
      <!-- lock down plugins versions to avoid using Maven defaults (may be moved to parent pom) -->
      <plugins>
        <!-- clean lifecycle, see https://maven.apache.org/ref/current/maven-core/lifecycles.html#clean_Lifecycle -->
        <plugin>
          <artifactId>maven-clean-plugin</artifactId>
          <version>3.1.0</version>
        </plugin>
        <!-- default lifecycle, jar packaging: see https://maven.apache.org/ref/current/maven-core/default-bindings.html#Plugin_bindings_for_jar_packaging -->
        <plugin>
          <artifactId>maven-resources-plugin</artifactId>
          <version>3.0.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.8.0</version>
        </plugin>
        <plugin>
          <artifactId>maven-surefire-plugin</artifactId>
          <version>2.22.1</version>
        </plugin>

        <plugin>
          <artifactId>maven-jar-plugin</artifactId>
          <version>3.0.2</version>
          <!-- Add main class entrance here!!!!! -->
          <configuration>
            <archive>
              <manifest>
                <addClasspath>true</addClasspath>
                <classpathPrefix>libs/</classpathPrefix>
                <mainClass>eroiko.ani.Main</mainClass>
              </manifest>
            </archive>
          </configuration>
        </plugin>

        <plugin>
          <artifactId>maven-install-plugin</artifactId>
          <version>2.5.2</version>
        </plugin>
        <plugin>
          <artifactId>maven-deploy-plugin</artifactId>
          <version>2.8.2</version>
        </plugin>
        <!-- site lifecycle, see https://maven.apache.org/ref/current/maven-core/lifecycles.html#site_Lifecycle -->
        <plugin>
          <artifactId>maven-site-plugin</artifactId>
          <version>3.7.1</version>
        </plugin>
        <plugin>
          <artifactId>maven-project-info-reports-plugin</artifactId>
          <version>3.0.0</version>
        </plugin>

        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-dependency-plugin</artifactId>
            <executions>
                <execution>
                    <id>copy-dependencies</id>
                    <phase>prepare-package</phase>
                    <goals>
                        <goal>copy-dependencies</goal>
                    </goals>
                    <configuration>
                        <outputDirectory>
                            ${project.build.directory}/libs
                            <!-- ${project.basedir}/libs -->
                        </outputDirectory>
                    </configuration>
                </execution>
            </executions>
        </plugin>

        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <shadedArtifactAttached>true</shadedArtifactAttached>
                        <transformers>
                            <transformer implementation=
                              "org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>eroiko.ani.Main</mainClass>
                        </transformer>
                    </transformers>
                </configuration>
                </execution>
            </executions>
        </plugin>

        <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-toolchains-plugin</artifactId>
          <executions>
            <execution>
              <goals>
                <goal>toolchain</goal>
              </goals>
              <configuration>
                <toolchains>
                  <jdk>
                    <version>15</version>
                  </jdk>
                </toolchains>
              </configuration>
            </execution>
          </executions>
        </plugin>

      </plugins>
```