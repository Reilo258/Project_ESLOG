package com.example.project_eslog;

import java.sql.Date;

public class Utility {
    final protected static char[] s_HexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

    protected static String Utility_ByteArrayToHexString(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length*2];
        int v;

        for(int j=0; j < bytes.length; j++)
        {
            v = bytes[j] & 0xFF;
            hexChars[j*2] = s_HexArray[v>>>4];
            hexChars[j*2 + 1] = s_HexArray[v & 0x0F];
        }

        return new String(hexChars);
    }

    protected static void Utility_ReverseByteArray(byte[] array) {
        int length = array.length;
        for (int i = 0; i < length / 2; i++) {
            byte temp = array[i];
            array[i] = array[length - 1 - i];
            array[length - 1 - i] = temp;
        }
    }



    protected static String Utility_LastReadingText(String now, String lastRead) {
        Date currTime = Date.valueOf(now);
        Date lastReading = Date.valueOf(lastRead);

        long secondsBetween = (currTime.getTime() - lastReading.getTime()) / 1000;

        if (secondsBetween < 10) return "Just now";
        else if (secondsBetween < 60) return secondsBetween + " seconds";
        else if (secondsBetween < 3600) return secondsBetween/60 + " minutes " + secondsBetween%60 + " seconds";
        else return secondsBetween/3600 + " hours " + secondsBetween%3600 + " minutes";
    }
}
