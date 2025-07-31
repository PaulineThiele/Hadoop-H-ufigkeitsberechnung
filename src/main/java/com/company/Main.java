package com.company;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.io.*;
import java.io.IOException;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import java.net.URI;
import java.net.URISyntaxException; 


public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException
    {
        long startTime = System.nanoTime();
        
        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Word Counter");

        //job.setNumReduceTasks(1);

        // set main class for the jar:
        job.setJarByClass(com.company.Main.class);

        // set the mapper and reducer classes
        job.setMapperClass(com.company.MyMapper.class);
        job.setReducerClass(com.company.MyReducer.class);
        job.setCombinerClass(com.company.MyReducer.class); // === Shuffle Class
        //job.setPartitionerClass(com.company.LangPartitioner.class);

        // output types (key, value)
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // set input/output paths:
        FileInputFormat.addInputPath(job, new Path(args[0]));

        //  check if exists:
        FileSystem fs = FileSystem.get(conf);

        Path outDir = new Path(args[1]);

        if (fs.exists(outDir)) {
            System.out.println("Already exists.. Overwriting");
            fs.delete(outDir, true);
        }
        FileOutputFormat.setOutputPath(job, outDir);

        conf.setBoolean("mapreduce.map.speculative", false);
        conf.setBoolean("mapreduce.reduce.speculative", false);

        String[] languages = {"Dutch", "English", "French", "German", "Italian", "Russian", "Spanish", "Ukrainian"};
        for (String lang : languages) {
            MultipleOutputs.addNamedOutput(job, lang, TextOutputFormat.class, Text.class, Text.class);
            job.addCacheFile(new URI("/user/pauline/stopwords/" + lang + ".json#" + lang + ".json"));
        }

        // run the job
        try {
            job.waitForCompletion(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("Done!");
        long endTime = System.nanoTime();
        long duration = endTime - startTime; 
        System.out.println("Duration:" + duration / 1_000_000 + " ms"); 
    }
}
