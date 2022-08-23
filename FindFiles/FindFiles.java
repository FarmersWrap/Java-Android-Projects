import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FindFiles {

    public static final String noMem = "Error: Memory runs out of stack, please narrow down the directory or disable -r option.";

    public static void showMenu() {
        String usage = "  Usage: java FindFiles filetofind [-option arg]";
        String help = "  -help                     :: print out a help page and exit the program.";
        String recurse = "  -r                        :: execute the command recursively in subfiles.";
        String regex = "  -reg                      :: treat `filetofind` as a regular expression when searching.";
        String directory = "  -dir [directory]          :: find files starting in the specified directory. ";
        String extension = "  -ext [ext1,ext2,...]      :: find files matching [filetofind] with extensions [ext1, ext2,...].";

        System.out.println(usage);
        System.out.println(help);
        System.out.println(recurse);
        System.out.println(regex);
        System.out.println(directory);
        System.out.println(extension);
    }
    public static boolean findFile(String fileName, String currDir, boolean useRec, boolean useReg,
                                boolean useExt, String[] exts, boolean notFound) throws Exception {

        File[] files = new File[0];
        try {
            files = new File(currDir).listFiles();
        } catch (Exception e) {
            throw new Exception(noMem);
        }
        
        List<File> fList = new ArrayList<File>();

        for (File file : files) {
            if (file.isFile()) {
                if (useReg) {
                    Pattern pattern = Pattern.compile(fileName);
                    Matcher matcher = pattern.matcher(file.getName());
                    if(matcher.find()) {
                        try {
                            fList.add(file);
                        } catch (Exception e) {
                            throw new Exception(noMem);
                        }
                    }
                } else if (file.getName().equals(fileName)){
                    try {
                        fList.add(file);
                    } catch (Exception e) {
                        throw new Exception(noMem);
                    }
                }
            }
        }

        for (File f : fList) {
            if (useExt) {
                for (String e : exts) {
                    if (f.getName().endsWith("." + e)) {
                        System.out.println(f.getCanonicalPath());
                        notFound = false;
                    }
                }
            } else {
                System.out.println(f.getCanonicalPath());
                notFound = false;
            }
        }

        for (File file : files) {
            if (file.isDirectory() && useRec) {
                boolean newSearch = findFile(fileName, file.getCanonicalPath(), useRec, useReg, useExt, exts, notFound);
                notFound = notFound && newSearch;
                
            }
        }
        return notFound;
    }

    public static void main(String[] args) {
        try {

            // Checks the number of args >= 1
            if (args.length < 1) {
                throw new Exception("");
            }

            // FileToFind
            String fileName = "";
            boolean fileNameGotten = false;
            boolean useRegex = false;
            boolean recursivelyFindFile = false;
            boolean useDir = false;
            String relativePath = "";
            boolean useExtension = false;
            String[] extensions = new String[0];
            String extString = "";
            String listOfArgs = "";

            for (int i = 0; i < args.length; i++) { 
                String option = args[i];
                if (option.equals("-help")) {
                    throw new Exception("");
                }
            }

            for (int i = 0; i < args.length; i++) {

                String option = args[i];
                if (option.equals("-r")) {
                    recursivelyFindFile = true;
                } else if (option.equals("-reg")) {
                    useRegex = true;
                } else if (option.equals("-dir")) {
                    useDir = true;
                    if ((i + 1) >= args.length) {
                        throw new Exception("Error: no valid relativePath passed, please follow the usage below.");
                    } else {
                        relativePath = args[i + 1];
                        i++;
                    }
                } else if (option.equals("-ext")) {
                    useExtension = true;
                    if ((i + 1) >= args.length) {
                        throw new Exception("Error: no valid extensions passed, please follow the usage below.");
                    } else {
                        extString = args[i + 1];
                        extensions = extString.split(",");
                        i++;
                    }
                } else if (Character.compare(option.charAt(0), '-') == 0) {
                    throw new Exception("Error: one of the arguments is not a valid option or your file name is not valid. \nPlease follow the usage below:");
                } else if (!fileNameGotten) {
                    fileName = option;
                    fileNameGotten = true;
                } else {
                    throw new Exception("Error: You have entered more than one filenames, please follow the usage below:");
                }

            }
            if (!fileNameGotten) {
                throw new Exception("Error: You have not entered a valid filename, please follow the usage below:");
            }

            String currentDirectory = System.getProperty("user.dir");
            if (useDir) {
                currentDirectory = currentDirectory + "/" + relativePath;
            }
            File dir = new File(currentDirectory);
            if (!dir.isDirectory()) {
                throw new Exception("Error: you are not passing in a valid relative directory path, please follow the usage below:");
            }


            String feedback = "FindFiles \"" + fileName + "\"";
            if (recursivelyFindFile) feedback += " -r";
            if (useRegex) feedback += " -reg";
            if (useDir) feedback += " -dir \"" + relativePath + "\"";
            if (useExtension) feedback += " -ext \"" + extString + "\"";
            System.out.println(feedback);
            try {
                boolean notFound = findFile(fileName, currentDirectory, recursivelyFindFile, useRegex, useExtension, extensions, true);
                if (notFound) System.out.println("Nothing found!");
            } catch (Exception e) {
                throw new Exception(noMem);
            }
            

        } catch (Exception e) {
            if (!e.getMessage().equals("")) {
                System.out.println(e.getMessage());
            }
            showMenu();
        }
    }
}

