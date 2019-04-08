# external-sort-large-file
Require: Jdk 8

- Build project

`mvn clean compile`

- Run project

`java -Xmx1G -classpath {your generated class path} SortFile {input_file_path} {output_file_path}`

generally -Xmx1g or -Xmx1G set max jvm heap size to 1Gb - default jvm 8 takes 1/4 ram.
