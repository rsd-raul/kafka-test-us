package com.mapr.examples;

import java.io.IOException;

/**
 * Pick whether we want to run as producer or consumer. This lets us
 * have a single executable as a build target.
 */
public class Run {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Must have either 'all' or a particular ID as argument");
        }
        switch (args[0]) {
            case "all":
                Consumer.main(args);
                break;
            default:
                Consumer.main(args);
        }
    }
}
