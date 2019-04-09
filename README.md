# external-sort-large-file
Require: Jdk 8

- Build project

`mvn clean compile`

- Run project: Sort File takes 2 input arguments

`java -Xmx1G -classpath {your generated class path} SortFile {input_file_path} {output_file_path}`

generally -Xmx1g or -Xmx1G set max jvm heap size to 1Gb, Xmx64M to set max jvm heap size to 64mb - default jvm 8 takes 1/4 ram.

practically minimum max-jvm-heap-size is usually at least 64mb
