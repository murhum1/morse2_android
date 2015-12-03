package morse.morseapp;

import android.util.Log;

import java.util.ArrayList;

import morse.morseapp.message.Alphabet;
import morse.morseapp.message.Encoder;
import morse.morseapp.utilities.Settings;

public class LightPostProcessor {

    public ArrayList<Blinker> blinkers = new ArrayList<Blinker>();
    public double lightMergeRadius = 2.61;
    public double light_merge_threshold;
    public int frameNumber = 0;

    public class BlinkerState
    {
        Boolean state;
        long timeStamp;

        public BlinkerState(Boolean state)
        {
            this.state = state;
            timeStamp = System.currentTimeMillis();
        }
    }

    public class Blinker // class that contains information about a blinker object
    {
        double x, y, brightness, size, mass;
        int ID, lastSeenFrame;
        boolean remove = false;
        ArrayList<BlinkerState> history = new ArrayList<BlinkerState>();
        public String morse = "";
        long lastSeenTime;

        public Blinker(DetectedLight light, int frame)
        {
            this.x = light.x;
            this.y = light.y;
            this.brightness = light.brightness;
            this.size = light.size;
            this.ID = light.ID;
            this.lastSeenFrame = frame;
            this.lastSeenTime = System.currentTimeMillis();
        }

        public double mergeValue(double val1, double val2, double mass1, double mass2) // get weighted average of two values
        {
            return (val1*mass1 + val2 * mass2) / (mass1+mass2);
        }

        public void merge(DetectedLight light, int frame) // merge newly detected light and previously seen blinker
        {
            x = mergeValue(light.x, x, 1.0, mass);
            y = mergeValue(light.y, y, 1.0, mass);
            brightness = mergeValue(light.brightness, brightness, 1.0, mass);
            mass += 1.0;
            lastSeenFrame = frame;
        }

        public void merge(Blinker other, int frame) // merge two blinkers
        {
            x = mergeValue(other.x, x, other.mass, mass);
            y = mergeValue(other.y, y, other.mass, mass);
            brightness = mergeValue(other.brightness, brightness, other.mass, mass);
            mass += other.mass;
            lastSeenFrame = frame;
        }

        public double getDistance(Blinker other)
        {
            double xdist = x-other.x;
            double ydist = y-other.y;
            return Math.sqrt(xdist*xdist + ydist*ydist);
        }

        public DrawText TranslateText(Encoder encoder, Alphabet alphabet) {
            String message = "";
            String[] letters = morse.split(" ");
            int letterIdx = 0;
            while(true) {
                if(letterIdx >= letters.length)
                    break;

                String letter = letters[letterIdx];
                message += alphabet.getChar(letter);
                letterIdx++;
            }
            return new DrawText((int)x, (int)y, message);
        }
    }

    public class DetectedLight // class that describes one pixel on the image that contains a light
    {
        int x, y, brightness, size, ID;
        public DetectedLight(int x, int y, int brightness, int size, int ID)
        {
            this.x = x;
            this.y = y;
            this.brightness = brightness;
            this.size = size;
            this.ID = ID;
        }

        public double getDistance(Blinker other)
        {
            double xdist = x-other.x;
            double ydist = y-other.y;
            return Math.sqrt(xdist*xdist + ydist*ydist);
        }
    }


    public void ProcessLights(int[] lightsList)
    {
        frameNumber++;
        //blinkers.clear();
        //first, loop over incoming new lights. Either merge them to existing blinkers or add them as new blinkers if none are close enough
        for(int i = 0; i < lightsList.length / 5; ++i)
        {
            DetectedLight curDetectedLight = new DetectedLight(lightsList[i*5+0], lightsList[i*5+1], lightsList[i*5+2], lightsList[i*5+3], lightsList[i*5+4]);

            boolean found = false;
            boolean anyInRange = false;
            int bestIdx = 0;
            int k = 0;
            double mincost = Settings.getMergeDistance();

            //loop over previously seen blinkers, set position and last seen time if close enough
            for (Blinker p : blinkers)
            {
                double length = curDetectedLight.getDistance(p);
                double cost = length / (p.size + curDetectedLight.size);
                if (cost < mincost)// && Math.abs(curDetectedLight.brightness - p.brightness) < light_merge_threshold)
                {
                    found = true;
                    mincost = cost;
                    bestIdx = k;
                }
                ++k;
            }

            //if we detected previously unknown blinker, add new light to blinkers list
            if (!found)
            {
                blinkers.add(new Blinker(curDetectedLight, frameNumber));
            }
            else if (found)
            {
                //else, update the position of found best blinker match
                blinkers.get(bestIdx).merge(curDetectedLight, frameNumber);
            }
        }


        //merge blinkers that are too close to each other
        for (Blinker p : blinkers)
        {
            for (Blinker q : blinkers)
            {
                if (p.ID == q.ID)
                    break;
                if(p.remove || q.remove)
                    continue;
                double length = q.getDistance(p);
                double cost = length / (p.size + q.size);
                if (cost < lightMergeRadius)// && Math.abs(q.brightness - p.brightness) < light_merge_threshold)
                {
                    Blinker smaller = p;
                    Blinker larger = q;
                    if (smaller.morse.length() > larger.morse.length()) //choose the older blinker, the other one gets removed
                    {
                        larger = p;
                        smaller = q;
                    }

                    smaller.remove = true;
                    larger.merge(smaller, frameNumber);
                }
            }
        }
        long currentTime = System.currentTimeMillis();
        float deltaTime = 1000.0f / Settings.getCharsPerSecond();

        // discard blinkers that have not been seen in a while, and removed blinkers
        ArrayList<Blinker> new_blinkers = new ArrayList<Blinker>();
        for (int i = 0; i < blinkers.size(); ++i)
        {
            Blinker p = blinkers.get(i);
            p.mass = 0;
            if (currentTime - p.lastSeenTime < 20 * deltaTime && !p.remove)
                new_blinkers.add(p);
        }
        blinkers.clear();
        for(Blinker p : new_blinkers)
                blinkers.add(p);

        //loop over blinkers and interpret their history to strings
        for (Blinker p : blinkers)
        {
            boolean state = p.lastSeenFrame == frameNumber;
            p.history.add(new BlinkerState(state));
            if(state)
                p.lastSeenTime = currentTime;

            String s = "";
            for (int i = 0; i < p.history.size(); ++i){
                boolean sign = p.history.get(i).state;
                long startTime = p.history.get(i).timeStamp;
                float len = .0f;
                while (true){
                    if (i >= p.history.size())
                        break;

                    if (p.history.get(i).state == sign)
                        len = p.history.get(i).timeStamp - startTime;
                    else
                        break;
                    i++;
                }
                if (!sign && len > 3*deltaTime)
                    s += " ";
                if(sign) {
                    if (len < 1.8f*deltaTime)
                        s += ".";
                    else if (len < 6*deltaTime)
                        s += "-";
                }
            }
            p.morse = s;
        }
    }
}
