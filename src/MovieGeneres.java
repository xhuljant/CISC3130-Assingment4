import java.io.*;
import java.util.*;

public class MovieGeneres {

    public static void main(String[] args) throws IOException {

        ArrayList<String> skippedDueToFormat= new ArrayList<>(); //some movies are skipped because the realease date or genre type is missing
        HashMap<String, Integer> allMovieGenres=new HashMap<>();
        HashMap<String, Integer> moviesLast5Years=new HashMap<>();
        HashMap<Integer, Integer> moviesPerYear=new HashMap<>();

        try {
            Scanner in = new Scanner(System.in);
            System.out.print("Enter File Path : " );
            String filePath=in.nextLine();
            BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
            int year = Calendar.getInstance().get(Calendar.YEAR); //gets whatever the current year is. USed later to find out from what year to add movies to 'moviesLast5Years'


            String line=br.readLine();

            while ((line = br.readLine()) != null) {


                String[] linearr = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); //splitting lines of file by comma ignoring the commas in parenthesis

                int movieID = 0;
                String title = "";
                int releaseYear = 0;
                ArrayList<String> genres = new ArrayList<String>();

                if (linearr[1].charAt(0) == '"') //some movie titles have a quotation mark. This line takes of quotes for easier proccesing
                    linearr[1] = linearr[1].substring(1, linearr[1].length() - 1);

                //if theres a space behind the release year the string is formatted to remove empty space
                if (linearr[1].charAt(linearr[1].length() - 1) == ' ') {
                    String hr = linearr[1].substring(0, linearr[1].length() - 1);
                    linearr[1] = hr;
                }

                for (int i = 0; i < linearr.length; i++) {

                    //splitting the second value of the spplit line array to get the values inside parenthesis(releasey ear which should be last spot in new array)
                    if (i == 1) {
                        String[] tempLine = linearr[1].split("[()]");
                        for (int j = 0; j < tempLine.length - 1; j++) {
                            title += tempLine[j];
                        }

                        try {
                            releaseYear = Integer.parseInt(tempLine[tempLine.length - 1]);
                        }catch (NumberFormatException e){
                           skippedDueToFormat.add(linearr[1]+linearr[2]); //some movies have multiple years or no years which are added to a seperate list to keep track of

                        }
                    }

                    if (i == 2) {
                        String[] tempLine = linearr[i].split("[|]");
                        for (String e : tempLine) {
                            genres.add(e);
                        }
                    }


                }

                if (releaseYear != 0) {

                    //checks to see if key exists in map and adds one to the value if it does exist, otherwise it adds a new key and value of 1
                    for (int i = 0; i < genres.size(); i++) {
                        if (allMovieGenres.containsKey(genres.get(i))) {
                            int temp = allMovieGenres.get(genres.get(i));
                            temp++;
                            allMovieGenres.put(genres.get(i), temp);
                        } else {
                            allMovieGenres.put(genres.get(i), 1);
                        }
                    }

                    //if the release year is 5 years ago or closer it adds movie map
                    if (releaseYear >= year - 5) {
                        for (int i = 0; i < genres.size(); i++) {
                            if (moviesLast5Years.containsKey(genres.get(i))) {
                                int temp = moviesLast5Years.get(genres.get(i));
                                temp++;
                                moviesLast5Years.put(genres.get(i), temp);
                            } else {
                                moviesLast5Years.put(genres.get(i), 1);
                            }
                        }
                    }

                    //checks to see if a movie with this release year is already in map and if not adds it to map with value of 1
                  if(moviesPerYear.containsKey(releaseYear)){
                      int temp=moviesPerYear.get(releaseYear);
                      temp++;
                      moviesPerYear.put(releaseYear, temp);
                  }else{
                      moviesPerYear.put(releaseYear, 1);
                  }

                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //sort function is described below
        allMovieGenres=sortByvalue(allMovieGenres);
        moviesLast5Years=sortByvalue(moviesLast5Years);

        //creating new files for each map and outputting values and keys to a new file
        File allGenresOutputFIle=new File("AllGenres.txt");
        FileWriter fw = new FileWriter(allGenresOutputFIle);
        for(String e:allMovieGenres.keySet()){
            //%=placeholder for string, -=puts strings to the left, 20s=20 spaces between strings
            fw.write(String.format("%-20s= %s", e, allMovieGenres.get(e))+"\n");

        }
        fw.close();

        File moviesInLast5Years=new File("MoviesInLastFiveYears.txt");
        fw=new FileWriter(moviesInLast5Years);
        for(String e:moviesLast5Years.keySet()){

            fw.write(String.format("%-20s= %s", e, moviesLast5Years.get(e))+"\n");

        }
        fw.close();

        File movPerYear = new File("MoviesPerYear.txt");
        fw=new FileWriter(movPerYear);
        for(int e:moviesPerYear.keySet()){
            fw.write(String.format("%-5s=%s", e, moviesPerYear.get(e))+"\n");
        }
        fw.close();

        File skippedMovies = new File("MoviesSkipped.txt");
        fw=new FileWriter(skippedMovies);
        for(String e:skippedDueToFormat) {
            fw.write(e + "\n)");
        }
        fw.close();


    }

    class Movie implements Comparable<Movie> {
        private int movieID;
        private String title;
        private int titleHash; //making title into a hashcode to make comparing titles easier in the future
        private int releaseYear;
        private ArrayList<String> genres = new ArrayList<String>(); //storing genres in a arraylist. easier to add genres without having to adjust array size

        public Movie(int movieID, String title, int releaseYear, ArrayList<String> genres) {
            this.title = title;
            this.releaseYear = releaseYear;
            this.movieID = movieID;
            this.genres = genres;
            this.titleHash = title.hashCode();
        }

        public int getTitleHash() {
            return titleHash;
        }

        public String getTitle() {
            return title;
        }

        public int getReleaseYear() {
            return releaseYear;
        }

        public String toString() {
            String temp = movieID + " " + title + releaseYear;
            for (int i = 0; i < genres.size(); i++) {
                temp += " " + genres.get(i);
            }

            return temp;
        }

        @Override
        public int compareTo(Movie other) {
            return this.getTitle().compareToIgnoreCase(other.getTitle());

        }
    }

    //HOW THE SORT METHOD WORKS'
    //method returns a new hashmap
    //linked list of values and keys of hashmap is made
    //linked list is sorted
    //foreach loop adds keys and values into a new temp map
    //temp map is returned to main where it can be intilized to unsorted map or new map
    public static HashMap<String, Integer> sortByvalue(HashMap<String, Integer> mapIn){
        List<Map.Entry<String, Integer> > list = new LinkedList<Map.Entry<String, Integer>>(mapIn.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
        for(Map.Entry<String, Integer> map : list){
            temp.put(map.getKey(), map.getValue());
        }

        return  temp;
    }

}