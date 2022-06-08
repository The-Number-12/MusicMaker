/*The point of this program is to proceduraly generate music that is structured similarly to human made songs. 
The songs will use common chord progressions, the beat will always be a slight variation of a 2-4 beat, 
notes will be in key and will alternate between matching notes in the current chord and not being out of chord but still in key.  */



import org.jfugue.player.Player;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import org.jfugue.midi.MidiFileManager;
import org.jfugue.pattern.Pattern;
import java.io.*;  
import java.io.File;

public class MakeSong {
    public static void main(String[] args) {

    
        Player player = new Player();
        String BPM = "T"+PickBPM();//"T120 ";
        String[] allNotes = {"A","A#","B","C","C#","D","D#","E","F","F#","G","G#","A","A#","B","C","C#","D","D#","E","F","F#","G","G#"};
        int baseNoteOfScale = RandomRange(0, 11);
        String[] notesInScale = findNotesInScale(baseNoteOfScale, allNotes);
        int[] chordProgression =PickChordProgressionFromList(); //{2,4,6,3};
        int[] verseChordProcression = PickVerseChords(chordProgression);
        int[] bridgeChords = GenerateBridgeChords();

        //lock all the chords to the chorus chords
        //in the futre it could be nice to generate these sepratly 
        verseChordProcression = chordProgression;
        bridgeChords = chordProgression;


        ArrayList<Integer> noteT = pickNoteTiming();
        ArrayList<Integer> notes = pickMelodyNotesTwo(noteT,true);
        ArrayList<Integer> noteT2 = pickNoteTiming();
        ArrayList<Integer> notes2 = pickMelodyNotesTwo(noteT2,true);

       ArrayList<Integer> verseNoteT = pickNoteTiming();
       ArrayList<Integer> verseNotes = pickMelodyNotesTwo(verseNoteT,false);
       ArrayList<Integer> verseNoteT2 = pickNoteTiming();
       ArrayList<Integer> verseNotes2 = pickMelodyNotesTwo(verseNoteT2,false);

        ArrayList<Integer> bridgeTiming = GenerateBridgeTiming();
        bridgeTiming.addAll(GenerateBridgeTiming());


        String bridgeChordsEncoded = EncodeNotesForChordProgression(notesInScale, bridgeChords, 4,70) + " ";

        // bt is beat timmings 
        ArrayList<Integer> bt1 = pickNoteTiming();
        ArrayList<Integer> bt2 = pickNoteTiming();
        ArrayList<Integer> bn1 = pickMelodyNotes(bt1);
        ArrayList<Integer> bn2 = pickMelodyNotes(bt2);



        String preChorusMelody = EncodeNoteProgression(new int[]{bridgeChords[0], bridgeChords[1]}, notesInScale, bt1, bn1, 5, false, 90);
        preChorusMelody += EncodeNoteProgression(new int[]{bridgeChords[2], bridgeChords[3]}, notesInScale, bt2, bn2, 5, false,90);

        preChorusMelody = GenerateBridgeMelody(chordProgression, notesInScale, 5, 90);


        String mainMelody = EncodeNoteProgression(new int[]{chordProgression[0],chordProgression[1]}, notesInScale, noteT, notes, 5,true,110) + " ";
        mainMelody += EncodeNoteProgression(new int[]{chordProgression[2],chordProgression[3]}, notesInScale, noteT2, notes2, 5,true,110) + " ";
        String introMelody = EncodeNoteProgression(new int[]{chordProgression[0],chordProgression[1]}, notesInScale, noteT, notes, 5,true,70) + " ";
        introMelody += EncodeNoteProgression(new int[]{chordProgression[2],chordProgression[3]}, notesInScale, noteT2, notes2, 5,true,70) + " ";
        String chordEncoded = EncodeNotesForChordProgression(notesInScale,chordProgression,4,90) + " ";
        String introChordEncoded = EncodeNotesForChordProgression(notesInScale,chordProgression,4,50) + " ";

        String verseMelody = EncodeNoteProgression(new int[]{verseChordProcression[0],verseChordProcression[1]}, notesInScale, verseNoteT, verseNotes, 5, false,80) + " ";
        verseMelody += EncodeNoteProgression(new int[]{verseChordProcression[0],verseChordProcression[1]}, notesInScale, verseNoteT2, verseNotes2, 5, false,80) + " ";
        String verseChordEncoded = EncodeNotesForChordProgression(notesInScale,verseChordProcression,4,60) + " ";

        int[] drumbBeat = makeDrumbBeat();
        String basicBeat = BasicDrumbBeat(drumbBeat,1);
        String verseBeat = BasicDrumbBeat(drumbBeat,0);
       
        String baseLine =  GenerateBaseLineTwo(drumbBeat,notesInScale,3);
        System.out.println(baseLine);
        baseLine = baseLine +" "+ baseLine +" ";
        basicBeat = basicBeat+" " + basicBeat +" ";
        verseBeat = verseBeat+" "+ verseBeat+" ";  
       // String noNotes = " rw  rw  rw  rw ";


     
        // add potental instramental changes for chorus and after intro
        // the end song will likely sound better if i find better instraments

        int[] melodyInstrs = new int[]{4,41,42,48,49,50,51,40,41};
        int[] melodyStronks = new int[]{0,1,24,29};
       
        // Only some of these sounds ok as sticato and syncopated so this maybe should get cut down
      
        int[] chordInstrs= new int[]{4,41,42,48,49,50,51};

        int[] chordStronk = new int[]{0,1,2,24,27,29,30,40,41,42};
        
        int[] baseInstr= new int[]{32,34,38};


        int chordInstra = 1;
        int chordChorusInstra =1;
        //0 = no, 2= just on chorous, 1= whole song 
        int strongChords = RandomRange(0, 1);
        if(strongChords==2){
            chordChorusInstra = chordStronk[RandomRange(0, chordStronk.length-1)];
            chordInstra = chordInstrs[RandomRange(0, chordInstrs.length-1)];
        }else if(strongChords==1){
            chordChorusInstra = chordStronk[RandomRange(0, chordStronk.length-1)];
            chordInstra =chordStronk[RandomRange(0, chordStronk.length-1)];
        }else{
            chordChorusInstra = chordInstrs[RandomRange(0, chordInstrs.length-1)];
            chordInstra = chordInstrs[RandomRange(0, chordInstrs.length-1)];
            
            int chordBeatState = RandomRange(0, 1);
        
            if(chordBeatState ==1){
                introChordEncoded = StaccatofyChord(introChordEncoded);
                verseChordEncoded = StaccatofyChord(verseChordEncoded);
                bridgeChordsEncoded = StaccatofyChord(bridgeChordsEncoded);;
                chordEncoded = StaccatofyChord(chordEncoded);
            }if(chordBeatState ==2){
                introChordEncoded = SyncopateChord(introChordEncoded);
                verseChordEncoded = SyncopateChord(verseChordEncoded);
                bridgeChordsEncoded = SyncopateChord(bridgeChordsEncoded);;
                chordEncoded = SyncopateChord(chordEncoded);
            }
        }

        
        int introMelInstra;
        int verseMelInstra;
        int preChorousmelInstra;
        int chorousMelInstra = melodyStronks[RandomRange(0, melodyStronks.length-1)];

        int strongMelody = 0;
        if(strongMelody ==1){
            int ins1 = melodyInstrs[RandomRange(0, melodyInstrs.length-1)];
            verseMelInstra = ins1;
            preChorousmelInstra = ins1;
            introMelInstra = melodyInstrs[RandomRange(0, melodyInstrs.length-1)];
        }else{
            int ins1 = melodyInstrs[RandomRange(0, melodyInstrs.length-1)];
            preChorousmelInstra = ins1;
            introMelInstra = ins1;
            int ins2 = melodyStronks[RandomRange(0, melodyStronks.length-1)];
            verseMelInstra = ins2;
            chorousMelInstra = ins2;
        }

        int weakMelody = RandomRange(0, 1);
       
        // In the futere it could be nice ot have a way to randomize the song lay out for now the layout will be fixed

        // Piano
        String melody =  " V0 I"+introMelInstra+" "+introMelody + " I"+ verseMelInstra + verseMelody + verseMelody + verseMelody + verseMelody+" I"+ preChorousmelInstra + preChorusMelody+ " I"+ chorousMelInstra + mainMelody + mainMelody+ " I"+ verseMelInstra + verseMelody + verseMelody+" I"+ preChorousmelInstra + preChorusMelody+ " I"+ chorousMelInstra + mainMelody + mainMelody +" I"+ preChorousmelInstra + preChorusMelody + preChorusMelody+ " I"+ chorousMelInstra + mainMelody+ mainMelody;
        String chords  = " V1 I"+chordInstra+" "+introChordEncoded +verseChordEncoded + verseChordEncoded +verseChordEncoded + verseChordEncoded + bridgeChordsEncoded +"I"+chordChorusInstra +" " +chordEncoded+chordEncoded+"I"+chordInstra +" " +verseChordEncoded + verseChordEncoded + bridgeChordsEncoded+"I"+chordChorusInstra +" "+chordEncoded+chordEncoded +"I"+chordInstra +" "+bridgeChordsEncoded + bridgeChordsEncoded+"I"+chordChorusInstra +" "+chordEncoded+chordEncoded;
        String base = " V2 I"+baseInstr[RandomRange(0, baseInstr.length-1)]+" " + " rw  rw  rw  rw "  +baseLine+ baseLine + baseLine +baseLine +baseLine+ baseLine + baseLine +baseLine +baseLine+ baseLine +baseLine+ baseLine + baseLine +baseLine +baseLine+ baseLine;
        String drumbs  = " V9 "+ " rw  rw  rw  rw "+ verseBeat+ verseBeat+ verseBeat+ verseBeat+ " rw  rw  rw  rw  "+ basicBeat+basicBeat + verseBeat+ verseBeat+ " rw  rw  rw  rw  "+ basicBeat+basicBeat+ " rw  rw  rw  rw  "+ " rw  rw  rw  rw  "+ basicBeat+basicBeat;
        
       
        
        Pattern songPattern = new Pattern(""+BPM + melody + chords +base+" " + drumbs);
        
        String path = System.getProperty("user.dir");        
        System.out.println("Working Directory = " + path);
        SaveGeneratedSong(songPattern);

        player.play( songPattern) ;
         
    }

    // we want to pick 120 as the bpm more than other values (since it is most common in music), This could be fully randomized, but it sounds better when it stays near common values
    public static int PickBPM(){
        int intBPM = 120;
        int r = RandomRange(0, 5);
        if(r ==5 ){
            intBPM =90;
        }
        if(r == 4){
            intBPM = 140;
        }
        if(r == 3){
            intBPM = 180;
        }
        System.out.println("Beats Per min set to"+ intBPM );
        return intBPM;
    }

    public static void SaveGeneratedSong(Pattern songPattern){       
        int fileNum = 0;
        try{
        Scanner s = new Scanner(new File("fileNums.txt"));
       
            if(s.hasNextInt()){
                fileNum = s.nextInt();
                System.out.println(" File Num ="+ fileNum);
            }
            s.close();
        }catch(IOException e){
            System.out.println("Things not working"+ e);
        }

        fileNum++;
        String songName = "song"+fileNum+".mid";
        

        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter("fileNums.txt"));
            writer.write(""+fileNum);
            writer.close();
        }catch(IOException e){
            System.out.println("Shit wentWrong with writing to fileNums.txt lol "+ e);
        }
        
        try{
            MidiFileManager.savePatternToMidi(songPattern, new File(songName));
        }catch(IOException e){
            System.out.println("I guess i need a try catch here "+ e);
        }

    }

    // syncopation annd stacato are both messed up right now, I think this is mainly because of bad midi sounds
    // they need to copy to output in sections
    public static String SyncopateChord(String inputChords){
        String output = ""; 
        String temp[] = new String[]{"","","",""};
        String alteredTemp[] = new String[]{"","","",""};
        int s =0;
        for(int i =0; i < inputChords.length(); ++i){

            if(inputChords.charAt(i)!='w'&&inputChords.charAt(i)!='W'){
                if( Character.isDigit(inputChords.charAt(i))&& !Character.isDigit(inputChords.charAt(i+1)) && inputChords.charAt(i+1)!= ' '&& inputChords.charAt(i+1)!= '+'){
                    char c = inputChords.charAt(i);
                    int atOctiv = Character.getNumericValue(c) - 1;
                   alteredTemp[s] += ""+ atOctiv;
                }else{
                    alteredTemp[s]+=inputChords.charAt(i);
                }
                temp[s] += inputChords.charAt(i);
            }else{
                alteredTemp[s] += 'i';
                temp[s]+= 'i';
            }
            if(Character.isDigit(inputChords.charAt(i))&&inputChords.charAt(i+1)==' '){
                s++;          
            }
            if(s >3) s = 3;

        }
        s=3;
        alteredTemp[s] += inputChords.charAt(inputChords.length()-1);
        temp[s] += inputChords.charAt(inputChords.length()-1);
        for(int i =0; i < 4; ++i){
            output += temp[i]+" ";
            output += alteredTemp[i]+" ";
            output += temp[i]+" ";
            output += alteredTemp[i]+" ";
            output += temp[i]+" ";
            output += alteredTemp[i]+" ";
            output += temp[i]+" ";
            output += alteredTemp[i]+" ";

        }


        return output;

    }
    public static String StaccatofyChord(String inputChords){
        String output = ""; 
        String temp[] = new String[]{"","","",""};
        int s =0;
        for(int i =0; i < inputChords.length(); ++i){
            if(inputChords.charAt(i)!='w'&&inputChords.charAt(i)!='W'){
                temp[s] += inputChords.charAt(i);
            }else{
                temp[s]+= 'i';
            }
            if(Character.isDigit(inputChords.charAt(i))&&inputChords.charAt(i+1)==' '){
                s++;          
            }
            if(s >3) s = 3;
        }
        for(int i =0; i < 4; ++i){
            output += temp[i]+" ";
            output += "ri ";
            output += temp[i]+" ";
            output += "ri ";
            output += temp[i]+" ";
            output += "ri ";
            output += temp[i]+" ";
            output += "ri ";
        }


        return output;
    }


    // debugTool
    public static int numBeatsInString(String input){
        int out = 0;
        for(int i =0; i < input.length();++i){
            if(input.charAt(i) == 'q'){
                out +=2;
            }
            if(input.charAt(i) == 'i'){
                out +=1;
            }
            if(input.charAt(i) == 'h'){
                out +=4;
            }
            if(input.charAt(i) == 'w'){
                out +=8;
            }


        }
        return out;
    }

    public static String  EncodeNoteProgression(int[] chordProgression, String[] notesInScale  ,ArrayList<Integer> noteT, ArrayList<Integer> notes, int octive, boolean isChorous, int velocity){
        String output = "";
        int chord = 0;
        int totalCount =0;
 
            int count =0;
            for(int x=0; x < notes.size(); ++x ){
          
                int tempOctive = octive;
                int baseNote =chordProgression[chord]-1;
                int noteToFind = baseNote + notes.get(x)-1;
                if(isChorous){
                    if(count + noteT.get(x)>6){
                        if(chord ==1) noteToFind = 0;
                        if(chord ==3) noteToFind = 0;
                    }
                }else{
                    // I should add somthing to avoid the tonic when we not in a chorous

                }
                if(noteToFind < 0) {
                     noteToFind = 7+(noteToFind);
                    tempOctive--;
                }
                if(noteToFind > 6){ 
                    noteToFind = noteToFind-7;
                    tempOctive++;
                }
                if(notes.get(x) > -9998){
                output += " "+ notesInScale[noteToFind];
                output+=""+tempOctive;
                }else{ output += " R";}

                
                if(noteT.get(x) ==0){
                    output+="q";
                    count+=2;
                    totalCount += 2;
                }else if(noteT.get(x) ==-1){
                    output+="i";
                    count+=1;
                    totalCount += 1;

                }
                else{
                    count += noteT.get(x);
                    totalCount += noteT.get(x);
                }
                if(noteT.get(x) ==1)output+="i";                                 
                if(noteT.get(x) == 2)output+="q";
                if(noteT.get(x) == 3)output+="qi";
                if(noteT.get(x) == 4)output+="h";
                if(noteT.get(x) == 5)output+="hi";
                if(noteT.get(x) == 6)output+="hq";
                output += "a"+velocity;
                
                
                if(count > 6){
                    
                    count =0;
                    chord++;
                    if(chord > 1){
                        chord =0;
                    }
                }
            }

        return output;
    }
    public static String GenerateBridgeMelody(int[] chordProgression,String[] notesInScale,int octive, int velocity){
        String output = "";
        for(int i =0; i < chordProgression.length; ++i){
            System.out.println(" added some notes to the bridge "+i);
            // get all the notes in the chord
            int[] chordN = new int []{chordProgression[i]-1,chordProgression[i]+1,chordProgression[i]+3};
            for(int x = 0 ; x < chordN.length; ++x){if(chordN[x] > 6)chordN[x] -=7;}

            output += " rq";
            int l = chordN[RandomRange(0, chordN.length-1)];
            output += " "+notesInScale[l]+octive+"i"+"a"+velocity;
            l += -1+(RandomRange(0, 1)*2);
            if(l <0)l = 6;
            if(l > 6)l= 0;
            output += " "+notesInScale[l]+octive+"i"+"a"+velocity;
            int skipedLastNote = RandomRange(0, 1);
            if(skipedLastNote ==0){
                l += -1+(RandomRange(0, 1)*2);
                if(l <0)l = 6;
                if(l > 6)l= 0;
                output += " "+notesInScale[l]+octive+"i"+"a"+velocity;
               
            }else{
                output += " ri";
            }
            if(skipedLastNote ==0){
                // skip this one
                output += " ri";
                l = chordN[RandomRange(0, chordN.length-1)];
                output += " "+notesInScale[l]+octive+"i"+"a"+velocity;
                l += -1+(RandomRange(0, 1)*2);
                if(l <0)l = 6;
                if(l > 6)l= 0;
                output += " "+notesInScale[l]+octive+"i"+"a"+velocity;
            }else{
                l = chordN[RandomRange(0, chordN.length-1)];
                output += " "+notesInScale[l]+octive+"i"+"a"+velocity;
                l += -1+(RandomRange(0, 1)*2);
                if(l <0)l = 6;
                if(l > 6)l= 0;
                output += " "+notesInScale[l]+octive+"i"+"a"+velocity;
                l += -1+(RandomRange(0, 1)*2);
                if(l <0)l = 6;
                if(l > 6)l= 0;
                output += " "+notesInScale[l]+octive+"i"+"a"+velocity;
            }

        }

        return output;
    }
    public static ArrayList<Integer> GenerateBridgeTiming(){
        ArrayList<Integer> bridgeTiming = new ArrayList<Integer>();
        bridgeTiming.add(0);
        bridgeTiming.add(1);
        bridgeTiming.add(1);
        bridgeTiming.add(-1+(RandomRange(0, 1)*2));
        if(bridgeTiming.get(3)==1){
            bridgeTiming.add(-1);

        }else{
        bridgeTiming.add(1);
        }
        bridgeTiming.add(1);
        bridgeTiming.add(1);


        return bridgeTiming;
    }

    public static int[] GenerateBridgeChords(){
        int[] output = new int[4];
        ArrayList<Integer> options = new ArrayList<Integer> ();
        options.add(3);
        options.add(6);
        options.add(7);
        options.add(5);

        for(int i = 0; i < output.length; ++i){
            int ran = RandomRange(0, options.size()-1);
            output[i] = options.get(ran);

            options.remove(ran);
        }

        return output;
    }
    public static String GenerateBaseLineTwo(int[] beats,String[] notesInScale, int octive){
        String output = ""; 
        for(int i = 0 ; i < beats.length; ++i){
            if(beats[i]!= 0){
                int a = i+1;
                while(a < beats.length && beats[a]==0){
                    a++;
                }
                a-= i;
                output += " "+notesInScale[0];
                if(a ==1)output+="i";                                 
                if(a == 2)output+="q";
                if(a == 3)output+="qi";
                if(a == 4)output+="h";
                if(a == 5)output+="hi";
                if(a == 6)output+="hq";


            }
        }
        return output;
    }
    public static String GenerateBaseLine(int[] beats,String[] notesInScale, int octive){
        String output = ""; 
        int numOffSteps = RandomRange(0, 4);
        int[] LocationsToStepAt = new int[numOffSteps];

        // this method is kind of bad because it can end up picking the same location to skip at twice
        for(int i =0; i < numOffSteps; ++i ){
            LocationsToStepAt[i] = RandomRange(1, 8);
        }
        int beat = 0;
        int countToNextBeat = 1;
        boolean hitNextBeat = false;
        for(int i =0; i < beats.length; ++i){

                countToNextBeat++;

            if(beats[i]!=0 ){
                beat++;
                if(!hitNextBeat){
                    hitNextBeat = true;
                }else{
                    hitNextBeat = false;
                    boolean step = false;
                    for(int x =0; x< LocationsToStepAt.length; ++x){
                        if(LocationsToStepAt[x]==beat){
                        step = true;
                        }
                    }
                    if(!step){
                        output += notesInScale[0];
                    }else{
                        output += notesInScale[3 +(RandomRange(0, 1)*2)];
                    }
                    output += octive;
                    if(countToNextBeat ==1)output+="i";                                 
                    if(countToNextBeat == 2)output+="q";
                    if(countToNextBeat == 3)output+="qi";
                    if(countToNextBeat == 4)output+="h";
                    if(countToNextBeat == 5)output+="hi";
                    if(countToNextBeat == 6)output+="hq";
                    countToNextBeat =0;
               
                    output+= " ";
                }
        }

        }
        System.out.println("base = "+output);

        return output;
    }

    public static int[] makeDrumbBeat(){
        int[] beat = {1,0,2,0,1,0,2,0,1,0,2,0,1,0,2,0}; 
        int noteToRemove = 8 + RandomRange(0, 3)*2;
        beat[noteToRemove] = 0;
        int noteToAdd = 8+ RandomRange(0, 3)*2 +1;
        beat[noteToAdd] = RandomRange(1, 2);
        return beat;
    }

    public static String BasicDrumbBeat(int[] beat, int snares){
        String output = "";
        // 1=kick, 2 = snare, 0=just highhat
        


        for(int i = 0; i < 16; ++i){
            String temp = " ";

            if(beat[i] == 0) temp += "ri";
            if(beat[i]>0) temp+="46i";
            if(beat[i] == 1) temp+="+37i";
            if(beat[i] ==2 && snares==1) temp+="+38i";
            output += temp;
        }
        System.out.println("Drumb= "+output);
        return output;
    }

    public static int[] PickChordProgressionFromList(){
        int [][] listOfChorousProgressions = {{1,4,6,5},{1,5,6,4},{1,6,4,5},{4,1,5,6},{5,6,4,1},{6,4,1,5}};
        return listOfChorousProgressions[RandomRange(0, listOfChorousProgressions.length-1)];
    }

    public static String EncodeNotesForChordProgression(String[] notesInScale, int[] chordProgression, int octive, int velocity){
        String output = "";
        for(int i = 0; i< chordProgression.length;++i){
            int s0 = chordProgression[i]-1;
            int s1 = chordProgression[i]+1;
            int s2 = chordProgression[i]+3;
            if(s1 > 6) s1 -=7;
            if(s2 > 6) s2 -=7;
            if(s0 > 6) s2 -=7;
            output += " "+notesInScale[s0]+ octive + 
            "w"+"a"+velocity+"+"+notesInScale[s1]+ octive +"w"+"a"+velocity+"+"+
             notesInScale[s2]+ octive+"w"+"a"+velocity+" ";
        }

        return output;
    }

    public static String[] findNotesInScale(int rootNote, String[] allNotes ){
        
        String [] notesInScale = new String[7];
        // set the notes in the key
        int i = 0;
        int c = rootNote;
        while(i < 7){
            notesInScale [i] = allNotes[c];
            if(c==(rootNote +4) || c == (rootNote +11)){
                c--;
            }
            c+=2;
            i++;
        }
        return notesInScale;
    }

    // this is only slightly better than the old version
    public static ArrayList<Integer> pickMelodyNotesTwo(ArrayList<Integer> noteT, boolean isChorous){
        
        int cutOff = 1;
        ArrayList<Integer> outputArray = new ArrayList<Integer>();
        ArrayList<Integer> notesInChord = new ArrayList<Integer>();
        notesInChord.add(1);
        notesInChord.add(3);
        notesInChord.add(5);
        
        ArrayList<Integer> notesNotInChord = new ArrayList<Integer>();
        notesInChord.add(0);
        notesNotInChord.add(2);
        notesNotInChord.add(4);
        notesNotInChord.add(6);
        if(isChorous){
        notesNotInChord.add(7);
        notesNotInChord.add(8);
        cutOff = 3;
        }

        int curNote = 1+(RandomRange(0, 2)*2);
        for(int i = 0; i < noteT.size(); i++){
            ArrayList<Integer> nic = notesInChord;
            ArrayList<Integer> nnc = notesNotInChord;
            if(noteT.get(i)==0){                
                outputArray.add(-9999);
                curNote = 1+(RandomRange(0, 2)*2);
            }else if(noteT.get(i)==-1){
                outputArray.add(-9998);
                curNote = 1+(RandomRange(0, 2)*2);
            }else if(curNote %2 ==1){
                int x =0;
                while( x< nnc.size()){
                    if(absoluteValue(curNote - nnc.get(x))>cutOff){
                        nnc.remove(x);
                    }else{
                        ++x;
                    }
                }
                curNote = nnc.get(RandomRange(0, nnc.size()-1));
                outputArray.add(curNote);
            }else{
                int x =0;
                while( x< nic.size()){
                    if(absoluteValue(curNote - nic.get(x))>cutOff){
                        nic.remove(x);
                    }else{
                        ++x;
                    }
                }
                curNote = nic.get(RandomRange(0, nic.size()-1));
                outputArray.add(curNote);

            }
        
        }

        return outputArray;
        
    }

    // this returns the notes to the melody, the notes are relitive to the root note of the chord (not the key)
    // this needs to have a more even distribution, currently it just hovers around the start note too much
    public static ArrayList<Integer> pickMelodyNotes(ArrayList<Integer> noteT){
        ArrayList<Integer> outputArray = new ArrayList<Integer>();
        int curNote = 1+(RandomRange(0, 2)*2);
        for(int i = 0; i < noteT.size(); i++){
            if(noteT.get(i)==0){
                outputArray.add(-9999);
                curNote = 1+(RandomRange(0, 2)*2);
            }else{
                outputArray.add(curNote);
                if(RandomRange(0, 1)==1){curNote--;}else{curNote++;}
                if(curNote>6) curNote -=2;
                if(curNote < 0) curNote +=2;
                
            }
        }

        return outputArray;
    }

    public static int absoluteValue(int i){
        if(i < 0){
            i = 0-i;
        }
        return i;
    }
    public static int[] PickVerseChords(int[] chorusCourds){
        int[] verseChords = new int[4];
        ArrayList<Integer> chordsToChoose = new ArrayList<Integer>();
        chordsToChoose.add(2);
        chordsToChoose.add(3);
        chordsToChoose.add(4);
        chordsToChoose.add(5);
        chordsToChoose.add(6);

        ArrayList<Integer> ctc  = new ArrayList<Integer>();
        ctc.addAll(chordsToChoose);
       

        ctc = RemoveElement(ctc, chorusCourds[3]);
      
        verseChords[0] = chordsToChoose.get(RandomRange(0, chordsToChoose.size()-1));
        int lastP = verseChords[0];
        int chordPos = 1;
        while(chordPos < 4){

           for(int i =0; i < chordsToChoose.size(); ++i){
               if(lastP== chordsToChoose.get(i)){
                   chordsToChoose.remove(i);
               }
           }
           ctc.clear();
            for(int i =2; i < 7; ++i){
                if(absoluteValue(lastP -i)==2 || absoluteValue(lastP -i)==3){
                    for(int a =0 ; a < chordsToChoose.size(); ++a){
                        if(chordsToChoose.get(a) == i) ctc.add(i);
                    }
                }
            }
            lastP = ctc.get(RandomRange(0, ctc.size()-1));
            
            verseChords[chordPos] = lastP;
            chordPos ++;
        }

        return verseChords;
    }

    // returns the arraylist but without the element that we dont want

    public static ArrayList<Integer> RemoveElement(ArrayList<Integer> in, int ele){
        int i =0;

        while( i < in.size()){
            if(in.get(i) == ele){
                in.remove(i);
            }else{
                i++;
            }
        }
        return in;

    }

    // 1 = 8th, 2 = 4th, 3= 3/8th 4= half, 0 = 8th rest 
    public static ArrayList<Integer> pickNoteTiming(){
        // pick if the pause is on the 1st or 2nd bar
        ArrayList<Integer> outputArray = new ArrayList<Integer>();      
        
        for(int c =1;c <= 2; c++){
            ArrayList<Integer> noteTiming = new ArrayList<Integer>();
            int numNotesInFirstBar = RandomRange(3, 6); //3 + (RandomRange(0, 1)*2);
            int numAdds = 7-numNotesInFirstBar;
; 
                             
            for(int i =0; i < numNotesInFirstBar; ++i){
                noteTiming.add(1);
            }
            for(int i =0; i < numAdds; ++i){
                int e = RandomRange(0, numNotesInFirstBar-1);
                noteTiming.set(e, noteTiming.get(e)+1);
            }  
            if(c==1)noteTiming.add(0,0);
            outputArray.addAll(noteTiming);          
        }
        return outputArray;
    }

    // saves me like 5 seconds each time i call this 
    // min and max are both inclusive
    public static int RandomRange(int min,int max ){
        if(min >= max +1){
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    
}
