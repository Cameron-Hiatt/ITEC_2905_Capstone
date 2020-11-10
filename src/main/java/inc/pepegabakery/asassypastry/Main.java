package inc.pepegabakery.asassypastry;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.channel.TextChannel;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


public class Main
{
    //Variables used in reading and writing things from files that contain moderation information and command responses.
    public static BufferedWriter newSelfPromoLinkWriter;
    public static Scanner inFileReader;

    //These two variables are used in checking daily for people's birthdays so the bot can announce it
    public static LocalDate todaysDate = LocalDate.now();
    public static Birthdays birthdays = new Birthdays();

    //Basically the object that bridges your code with the discord bot, also logs the bot in.
    public static DiscordApi api = new DiscordApiBuilder().setToken("NzY1OTc1MTU3MzY5NDcxMDM2.X4cn9A.LWcZVkaND_X_HU9bN78w4AG1sK8").login().join();

    //text channels the bot uses to send specific messages to
    public static Optional<TextChannel> communityChat = api.getTextChannelById("524120107828510723");
    public static Optional<TextChannel> selfPromos = api.getTextChannelById("728748179079692369");
    public static Optional<TextChannel> thePrivateOffice = api.getTextChannelById("770674840541462529");

    public static void main(String[] args) throws ExecutionException, InterruptedException
    {
        //Random object used to randomize command responses.
        Random rand = new Random();

        //creating a thread-safe versions of an ArrayList to contain moderation information and command responses
        CopyOnWriteArrayList<String> selfPromosLinks = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<String> duplicateLinkResponses = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<String> theBlackListArray = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<String> noNoWordsArray = new CopyOnWriteArrayList<>();
        CopyOnWriteArrayList<String> magic8BallArray = new CopyOnWriteArrayList<>();

        //Creating a BirthdayTask and running said task with the discord api's scheduler to check for birthdays every day
        BirthdayTask task = new BirthdayTask();
        api.getThreadPool().getScheduler().scheduleWithFixedDelay(task, 0, 24, TimeUnit.HOURS);

        //Reading in needed information from text files to their respective arrays to be used by the bot for moderating
        //messages and to give it a pool of responses for commands.
        try
        {
            newSelfPromoLinkWriter = new BufferedWriter(new FileWriter("SelfPromoLinks.txt", true));
            inFileReader = new Scanner((new File("SelfPromoLinks.txt")), "utf-8");

            while(inFileReader.hasNext())
            {
                selfPromosLinks.add(inFileReader.nextLine());
            }

            inFileReader.close();

            inFileReader = new Scanner((new File("TheBlackList.txt")), "utf-8");

            while(inFileReader.hasNext())
            {
                theBlackListArray.add(inFileReader.nextLine());
            }

            inFileReader.close();

            inFileReader = new Scanner((new File("NoNoWords.txt")), "utf-8");

            while(inFileReader.hasNext())
            {
                noNoWordsArray.add(inFileReader.nextLine());
            }

            inFileReader.close();

            inFileReader = new Scanner((new File("8Ball.txt")), "utf-8");

            while(inFileReader.hasNext())
            {
                magic8BallArray.add(inFileReader.nextLine());
            }

            inFileReader.close();

        } catch (IOException e1) {
            System.out.println(e1.toString());
        }

        //filling the duplicateLinkResponses array with options it can choose from when duplicate links are posted
        duplicateLinkResponses.add("STOP, criminal scum! :police_officer:");
        duplicateLinkResponses.add("Woah there partner! Ya done did post that already :thumbsup:");
        duplicateLinkResponses.add("Miss me with that spam :wave:");
        duplicateLinkResponses.add("Hold up! Wait a minute... :thinking: something ain't right...");
        duplicateLinkResponses.add("how bout NO!");
        duplicateLinkResponses.add("*Tevin consumes the duplicate link*");
        duplicateLinkResponses.add("<:pepeSus:736058074065928282>");
        duplicateLinkResponses.add("*Tevin didn't like that*");
        duplicateLinkResponses.add("It appears you have posted a duplicate link, it would be a shame if I... *butt chuggs duplicate link*... engulfed it ( ͡° ͜ʖ ͡°)");
        duplicateLinkResponses.add("Can you not <:monkaW:710417487031828500>");
        duplicateLinkResponses.add("\"Tevin had never *SEEN* such degeneracy before! <:weirdChamp:710417531365621760>\"");

        //A message listener for messages posted in selfPromos that deletes duplicate link posts.
        selfPromos.ifPresent(textChannel -> textChannel.addMessageCreateListener(event ->
        {
            if (event.getMessageContent().contains("/"))
            {
                for (int i = 0; i < selfPromosLinks.size(); i++)
                {
                    if (event.getMessageContent().contains(selfPromosLinks.get(i)))
                    {
                        event.getChannel().sendMessage(duplicateLinkResponses.get(rand.nextInt(duplicateLinkResponses.size())));
                        event.getMessage().delete();
                        i = selfPromosLinks.size();
                    } else if (i == selfPromosLinks.size() - 1)
                    {
                        selfPromosLinks.add(event.getMessageContent());
                        try
                        {
                            newSelfPromoLinkWriter.write(event.getMessageContent());
                            newSelfPromoLinkWriter.newLine();
                            newSelfPromoLinkWriter.flush();
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        i = selfPromosLinks.size();
                    }
                }
            }
        }));//end of selfPromos text channel listener

        //This is a server-wide message listener. It listens for banned words and phrases as well as commands.
        //Conditions are in place so the bot cannot use his own commands. (For example, when he responds with them to !tevin)
        api.addMessageCreateListener(event -> {
            //This for loop checks messages for pornographic mentions or websites, if found it deletes it and notifies the admins
            for(int i = 0; i < theBlackListArray.size(); i++)
            {
                if(event.getMessageContent().toLowerCase().contains(theBlackListArray.get(i))) {

                    event.getMessage().delete();
                    event.getMessage().getChannel().sendMessage("<:weirdChamp:710417531365621760>");
                    thePrivateOffice.get().sendMessage(event.getMessageAuthor().toString() + " :arrow_left: This heretic tried " +
                            "to post something with" +
                            " some instance of p0rn in it. Possibly harmless, I flagged it. Tevin good boy.");
                    i = theBlackListArray.size();
                }
            }

            //This for loop checks for words the admins have decided were too much and shouldn't be in the server, and deletes it if found.
            for(int i = 0; i < noNoWordsArray.size(); i++)
            {
                if(event.getMessageContent().toLowerCase().contains(noNoWordsArray.get(i))) {

                    event.getMessage().delete();
                    event.getMessage().getChannel().sendMessage("*TEVIN CONSUMES THE DEGENERATE MESSAGE*");
                    i = noNoWordsArray.size();
                }
            }

            //!whoistevin command
            if(event.getMessageContent().toLowerCase().contains("!whoistevin") &&
                    !event.getMessageAuthor().getIdAsString().contentEquals("765975157369471036"))
            {
                event.getMessage().getChannel().sendMessage("Tevin, the humanoid watchdog of Pepega Bakery Inc. that " +
                        "protects from violators and gives advice, thirsts for blood.");
            }

            //what do you say tevin command
            if(event.getMessageContent().toLowerCase().contains("what do you say tevin") &&
                    !event.getMessageAuthor().getIdAsString().contentEquals("765975157369471036"))
            {
                event.getMessage().getChannel().sendMessage(magic8BallArray.get(rand.nextInt(magic8BallArray.size())));
            }

            //!tevin command
            if(event.getMessageContent().toLowerCase().contains("!tevin")  &&
                    !event.getMessageAuthor().getIdAsString().contentEquals("765975157369471036"))
            {
                event.getMessage().getChannel().sendMessage("Tevin currently responds to:\n" +
                        "\n-!WhoIsTevin: This will tell you Tevin's bio\n" +
                        "\n-What do you say Tevin: This will give you a randomized response completely out of context\n" +
                        "\n-!tevin: this will give you a list of commands Tevin will respond to\n" +
                        "\nTevin also currently moderates message contents from various things. Commands are not case sensitive btw.");
            }

        });//end of api message listener (all messages sent in the discord server are listened to)

        // Print the invite url of your bot, needed if one is to invite the bot to their server. For now, commented out.
        //System.out.println("You can invite the bot by using the following url: " + api.createBotInvite());

    }//end of main method

    //BirthdayTask class that announces birthdays on the day of someone's known and recorded birthday in the discord server.
    static class BirthdayTask implements Runnable
    {

        public BirthdayTask()
        {
            todaysDate = LocalDate.now();
        }

        @Override
        public void run()
        {
            todaysDate = LocalDate.now();

            CopyOnWriteArrayList<String> todaysBirthdays = new CopyOnWriteArrayList<>(birthdays.checkIfBirthday(todaysDate));

            if(todaysBirthdays.size() != 0)
            {
                for (int i = 0; i < todaysBirthdays.size(); i++) {
                    communityChat.get().sendMessage("Today is <@" + todaysBirthdays.get(i) + "> birthday!");
                }
            }

        }//end of run
    }//end of class BirthdayTask

}//end of class Main
