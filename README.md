# ap-final-project
Shopping Mall - Java Swing demo

Team:
- Paria Bagheri Behrouzian — student id: 40312013
- Matin Zaki — student id: 40313017

## How to run
1. Put gson-2.10.1.jar into the lib/ directory.
2. Compile: javac -cp "lib\gson-2.10.1.jar" -d out @(Get-ChildItem src -Recurse -Filter *.java | ForEach-Object { $_.FullName })
3. Run: java -cp "out;lib\gson-2.10.1.jar" com.apfinal.ui.Main
