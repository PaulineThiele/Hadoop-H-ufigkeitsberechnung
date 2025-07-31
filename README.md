# Hadoop Häufigkeitsberechnung 

## Aufgabenstellung 
Die zu lösende Aufgabe bestand darin, die Häufigkeit aller Wörter in einem mehrsprachigen Textdatensatz mittels Hadoop zu ermitteln. Dabei sollten Wörter, die in Texten oft vorkommen aber keine wichtige inhaltliche Bedeutung aufweisen, sogenannte Stoppwörter [1], von der Häufigkeitsberechnung ausgeschlossen werden.  Solche Wörter sind beispielsweise “a”, “an”, “the”, “of”, “und”, “mit”, “la”, “и” oder “же”. Die Ergebnisse der gezählten Vorkommenshäufigkeiten sollten als TOP-10 Liste pro Sprache ausgegeben werden. 

## Projektstruktur
Das Projekt ist wie folgt strukturiert: 

```bash
Hadoop Häufigkeitsberechnung/
│
├── data/                          # contains datasets
│   ├── dataset_1GB
│   ├── dataset_250MB
│   ├── dataset_500MB
│   └── dataset_original
│
├── src/main/java/com/company      # java code 
│   ├── Main.java
│   ├── MyMapper.java
│   └── MyReducer.java       
│
├── target/                        # contains .jar file with maven packages
│   ├── ...          				# maven specific directories 
│   └── my-hadoop-project-1.0-SNAPSHOT.jar 
│
├── README.md
├── Log.txt 						# log file 
└── run_all_languages.sh 			# bash script to start the code 
```

## Datensatz
Der Originaldatensatz wurde mehrfach vervielfältigt, sodass drei weitere Datensätze erstellt wurden. 
Diese können aus der .zip Datei heruntergeladen werden. 

## Projekt starten 

### Voraussetzungen 
- Hadoop ist installiert 
- Git-Repository ist gecloned und liegt im Hadoop-Ordner 
- HDFS und YARN sind gestartet 
- es befinden sich Dateien im HDFS Input-Ordner (hdfs dfs-put datasetX /user/xyz/input)


Um das Projekt zu starten, wechseln Sie zuerst in das Verzeichnis, wo das Projekt liegt. 

Anschließend führen Sie folgendes Kommando im Terminal aus: 
```bash 
./run_all_languages.sh
```
