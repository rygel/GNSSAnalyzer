import net.sf.marineapi.nmea.event.SentenceEvent;
import net.sf.marineapi.nmea.event.SentenceListener;
import net.sf.marineapi.nmea.io.SentenceReader;
import net.sf.marineapi.nmea.sentence.GGASentence;
import net.sf.marineapi.nmea.sentence.GSVSentence;
import net.sf.marineapi.nmea.sentence.SentenceId;
import net.sf.marineapi.nmea.util.SatelliteInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by brandt on 17.09.2016.
 */
public class GNSSAnalyzer implements SentenceListener {

    private SentenceReader reader;
    private Map<String, Integer> svCount = new TreeMap<String, Integer>();

    /**
     * Creates a new instance of FileExample
     *
     * @param file File containing NMEA data
     */
    public GNSSAnalyzer(File file) throws IOException {

        // create sentence reader and provide input stream
        InputStream stream = new FileInputStream(file);
        reader = new SentenceReader(stream);

        // register self as a listener for GGA sentences
        reader.addSentenceListener(this, SentenceId.GGA);
        reader.addSentenceListener(this, SentenceId.GSV);
        reader.start();
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.event.SentenceListener#readingPaused()
     */
    public void readingPaused() {
        System.out.println("-- Paused --");
        for(Map.Entry<String, Integer> entry : svCount.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.event.SentenceListener#readingStarted()
     */
    public void readingStarted() {
        System.out.println("-- Started --");
    }

    /*
     * (non-Javadoc)
     * @see net.sf.marineapi.nmea.event.SentenceListener#readingStopped()
     */
    public void readingStopped() {
        System.out.println("-- Stopped --");
        for(Map.Entry<String, Integer> entry : svCount.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
    }

    /*
     * (non-Javadoc)
     * @see
     * net.sf.marineapi.nmea.event.SentenceListener#sentenceRead(net.sf.marineapi
     * .nmea.event.SentenceEvent)
     */
    public void sentenceRead(SentenceEvent event) {

        // Safe to cast as we are registered only for GGA updates. Could
        // also cast to PositionSentence if interested only in position data.
        // When receiving all sentences without filtering, you should check the
        // sentence type before casting (e.g. with Sentence.getSentenceId()).
        //System.out.println(event.getSentence().getSentenceId());
        String sentenceID = event.getSentence().getSentenceId();
        if (sentenceID.equals("GGA")) {
            GGASentence s = (GGASentence) event.getSentence();
            // Do something with sentence data..
            //System.out.println(s.getPosition());
        } else if(sentenceID.equals("GSV")) {
            GSVSentence s = (GSVSentence) event.getSentence();
            // Do something with sentence data..
            List<SatelliteInfo> satelliteInfo = s.getSatelliteInfo();
            for(SatelliteInfo info: satelliteInfo) {
                Object count = svCount.get(info.getId());
                if (count == null) {
                    svCount.put(info.getId(), new Integer(1));
                } else {
                    Integer newCount = (Integer)count + 1;
                    svCount.put(info.getId(), newCount);
                }
                //System.out.println("ID: " + info.getId());
            }

        }



    }

    /**
     * Main method takes one command-line argument, the name of the file to
     * read.
     *
     * @param args Command-line arguments
     */
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Example usage:\njava FileExample nmea.log");
            System.exit(1);
        }

        try {
            new GNSSAnalyzer(new File(args[0]));
            System.out.println("Running, press CTRL-C to stop..");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

