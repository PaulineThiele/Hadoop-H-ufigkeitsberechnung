#!/bin/bash


for lang in Dutch English French German Italian Russian Spanish Ukrainian
do
  echo "Starte Job für $lang ..."

  if hdfs dfs -test -d /user/pauline/output/$lang; then
    hdfs dfs -rm -r /user/pauline/output/$lang
  fi

  # run code 
  start=$(date +%s)
  hadoop jar target/my-hadoop-project-1.0-SNAPSHOT.jar com.company.Main /user/pauline/input/$lang /user/pauline/output/$lang
  end=$(date +%s)
  duration=$((end - start))
  
  size=$(hdfs dfs -du -s /user/pauline/input/$lang | awk '{print $1}')
  echo "$lang Größe: $size Bytes"
  size_mb=$(echo "scale=2; $size/1024/1024" | bc)
  echo "$lang Größe: $size_mb MB"
  echo "$(date +"%Y-%m-%d %H:%M:%S") $lang Größe: $size_mb MB">>Log.txt
  echo "$lang Dauer: $duration Sekunden"
  echo "$(date +"%Y-%m-%d %H:%M:%S") $lang Dauer: $duration Sekunden">>Log.txt

  # merge results 
  if hdfs dfs -test -e /user/pauline/output/$lang/${lang}-merged.txt; then
    hdfs dfs -rm /user/pauline/output/$lang/${lang}-merged.txt
  fi

  hadoop fs -cat /user/pauline/output/$lang/${lang}-m-* | \
  awk '{count[$1]+=$2} END {for (w in count) print w, count[w]}' > ${lang}_merged.txt
  hdfs dfs -put ${lang}_merged.txt /user/pauline/output/$lang/${lang}-merged.txt

  echo "$(date +"%Y-%m-%d %H:%M:%S") merged $lang">>Log.txt

  # print and save results
  echo " "
  echo "TOP 10 Wörter für $lang:"
  hdfs dfs -cat /user/pauline/output/$lang/${lang}-merged.txt | sort -k2 -nr | head
  echo "$(date +"%Y-%m-%d %H:%M:%S") TOP 10 Wörter für $lang:">>Log.txt
  top=$(hdfs dfs -cat /user/pauline/output/$lang/${lang}-merged.txt | sort -k2 -nr | head)
  echo "$(date +"%Y-%m-%d %H:%M:%S") $top">>Log.txt
done