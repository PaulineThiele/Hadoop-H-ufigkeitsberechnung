package com.company;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import java.io.IOException;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;

public class MyReducer extends Reducer<Text, IntWritable, Text, IntWritable> {

    private MultipleOutputs<Text, IntWritable> mos;

    @Override
    protected void setup(Context context) {
        mos = new MultipleOutputs<>(context);
    }

    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException
    {
        String[] parts = key.toString().split(":", 2);
        if (parts.length != 2) {
            // ignore invalid format 
            return;
        }
        String lang = parts[0];
        String word = parts[1];

        int sum = 0;

        for (IntWritable val : values)
        {
            sum += val.get();
        }

        mos.write(lang, new Text(word), new IntWritable(sum));
    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
        mos.close();
    }
}
