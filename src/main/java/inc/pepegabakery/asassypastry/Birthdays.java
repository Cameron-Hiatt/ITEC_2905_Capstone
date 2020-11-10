package inc.pepegabakery.asassypastry;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Birthdays
{
    ConcurrentHashMap<LocalDate, String> userBirthdays = new ConcurrentHashMap<>();

    Birthdays()
    {
        LocalDate bday1 = LocalDate.of(2005, 7, 27);
        LocalDate bday2 = LocalDate.of(2004, 12, 11);
        LocalDate bday3 = LocalDate.of(1996, 12, 2);
        LocalDate bday4 = LocalDate.of(2001, 12, 31);
        LocalDate bday5 = LocalDate.of(1998, 7, 9);
        LocalDate bday6 = LocalDate.of(1998, 4, 7);
        LocalDate bday7 = LocalDate.of(1998, 7, 9);
        LocalDate bday8 = LocalDate.of(1997, 11, 13);
        LocalDate bday9 = LocalDate.of(1997, 12, 17);
        LocalDate bday10 = LocalDate.of(2006, 12, 18);
        LocalDate bday11 = LocalDate.of(2005, 11, 21);
        LocalDate bday12 = LocalDate.of(2005, 1, 11);
        LocalDate bday13 = LocalDate.of(1997, 2, 11);
        LocalDate bday14 = LocalDate.of(2002, 5, 19);

        //LocalDate testBDay1 = LocalDate.of(2001, 11, 9);
        //LocalDate testBDay2 = LocalDate.of(1989, 12, 9);

        userBirthdays.put(bday1, "513114585373474827");
        userBirthdays.put(bday2, "712095936222068806");
        userBirthdays.put(bday3, "384589199178727426");
        userBirthdays.put(bday4, "167763299927195648");
        userBirthdays.put(bday5, "637740738179760128");
        userBirthdays.put(bday6, "543296271335817237");
        userBirthdays.put(bday7, "562848503357898752");
        userBirthdays.put(bday8, "154418282382753792");
        userBirthdays.put(bday9, "524120218893680640");
        userBirthdays.put(bday10, "670573726424629248");
        userBirthdays.put(bday11, "695830352454746164");
        userBirthdays.put(bday12, "475232935616839680");
        userBirthdays.put(bday13, "180180393331392522");
        userBirthdays.put(bday14, "452598759340244993");

        //userBirthdays.put(testBDay1, "524120218893680640"); this uses cameron's discord ID
        //userBirthdays.put(testBDay2, "154418282382753792");   this uses Magnus' discord ID
    }

    /**
     * The checkIfBirthday() method receives a LocalDate variable representing the current day's date and uses that to
     * check all the keys in the userBirthdays HashMap for a match. If found, it adds the key's corresponding userID
     * to an ArrayList. Once all the matching birthday's to the current day's date are found and added into the
     * ArrayList, it sends back the collected userIDs to be used in an announcement message to the Discord server
     * that it is said person's birthday.
     *
     * @param recievedBday
     * @return CopyOnWriteArrayList<String> todaysBirthdays </String>
     */
    public CopyOnWriteArrayList<String> checkIfBirthday(LocalDate recievedBday)
    {
        CopyOnWriteArrayList<String> todaysBirthdays = new CopyOnWriteArrayList<>();
        for (LocalDate key : userBirthdays.keySet())
        {
            if(recievedBday.getMonthValue() == key.getMonthValue() && recievedBday.getDayOfMonth() == key.getDayOfMonth())
            {
                todaysBirthdays.add(userBirthdays.get(key));
            }
        }
        return todaysBirthdays;
    }
}
