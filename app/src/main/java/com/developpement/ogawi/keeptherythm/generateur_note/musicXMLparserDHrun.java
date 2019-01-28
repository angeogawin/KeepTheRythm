package com.developpement.ogawi.keeptherythm.generateur_note;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by Dorien Herremans on 05/02/15.
 */

public class musicXMLparserDHrun {

    public static void main (String[]args)throws IOException {

        System.out.println("Reading in file... ");
        String[] songSequence = null;
        String[] songSequenceParsed = null;
        String filename = "C:\\Users\\ogawi\\Desktop\\tobias_weber_rescue_me_instrumental";

        if (args.length > 0) {
            filename = args[0];

        } else {
            System.out.print("You did not specify any filename as on option.");
            System.exit(0);
        }
        //filename = "./data/test.xml";

        musicXMLparserDH parser = new musicXMLparserDH(filename);



        //prints out the note sounding at the same slice (each division of the musicxml file
        String[] flatSong = parser.parseMusicXML();

        //print out the songI j
        if (args.length > 1) {
            if (args[1] == "-print") {
                for (int i = 0; i < flatSong.length; i++) {
                    System.out.println(flatSong[i]);
                }
            }
        }




        //returns an ArrayList containing all the note objects
        ArrayList<Note> songSequenceOfNoteObjects = parser.getNotesOfSong();

        System.out.print("test");


    }
}
