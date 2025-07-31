package com.company;

import java.io.*;
import java.util.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.fs.Path;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class MyMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private Text langWord = new Text();
    private Map<String, Set<String>> stopwordsByLang = new HashMap<>();

    @Override
    protected void setup(Context context) throws IOException {

        String[] languages = {"Dutch", "English", "French", "German", "Italian", "Russian", "Spanish", "Ukrainian"}; 
        for (String lang : languages) {
            File stopwordFile = new File(lang + ".json"); 
            if (stopwordFile.exists()) {
                try {
                    String json = new String(Files.readAllBytes(stopwordFile.toPath()), StandardCharsets.UTF_8);
                    JSONParser parser = new JSONParser();
                    JSONArray arr = (JSONArray) parser.parse(json);
                    Set<String> stopSet = new HashSet<>();
                    for (Object o : arr) {
                        stopSet.add(o.toString().toLowerCase());
                    }
                    stopwordsByLang.put(lang, stopSet);
                } catch (Exception e) {
                    throw new IOException("Error parsing stopwords for lang " + lang, e);
                }
            }
        }
    }

    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException
    {
        String filePathStr = ((FileSplit) context.getInputSplit()).getPath().toString();
        Path filePath = new Path(filePathStr);
        Path parent = filePath.getParent();
        String lang = parent.getName(); // gets last directory name 

        Set<String> stopwords = stopwordsByLang.getOrDefault(lang, Collections.emptySet());

        String[] tokens = value.toString().split("\\P{L}+");

        for(String token : tokens)
        {
            String word = token.toLowerCase();

            if (!word.isEmpty() && !stopwords.contains(word)) {

                langWord.set(lang + ":" + word);
                context.write(langWord, new IntWritable(1));
            }
        }

    }
}
