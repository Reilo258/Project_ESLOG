package com.example.project_eslog;

import android.nfc.TagLostException;
import android.nfc.tech.MifareUltralight;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NfcComm {
    public static List<String> temp_array = new ArrayList<String>();
    public static List<String> date_array = new ArrayList<String>();
    public static String formattedTime;
    protected static void NfcComm_ReadNDEF()
    {
        int blocksToRead = 0;
        int rest = 0;
        int ndefLength = 0;
        int offset = 0;
        byte[] ndefMessage;
        int typeLength = 0;
        int payloadLength = 0;
        byte[] payloadType;
        byte[] ndefPayload;
        byte[] temperature_raw;

        if(MainActivity.s_MyTag != null) {
            MifareUltralight uTag = MifareUltralight.get(MainActivity.s_MyTag);
            try {
                uTag.connect();
                if (uTag.isConnected()) {
                    byte[] data = uTag.readPages(4);
                    if (data != null) {
                        if(data[0] == (byte) 0x03) {
                            ndefLength = data[1]&0xFF;
                            if(ndefLength != 0) {
                                ndefMessage = new byte[ndefLength];
                                System.arraycopy(data, 2, ndefMessage, 0, 14);
                                blocksToRead = ((ndefLength - 14 + 16 - 1) / 16);
                                rest = (ndefLength - 14) % 16;
                                for (int i = 0; i < blocksToRead; i++) {
                                    data = uTag.readPages(8 + 4 * i);
                                    if (i < blocksToRead - 1) {
                                        System.arraycopy(data, 0, ndefMessage, 14 + i * 16, 16);
                                    }
                                    else if (i == blocksToRead - 1) {
                                        if(rest == 0) System.arraycopy(data, 0, ndefMessage, 14 + i * 16, 16);
                                        else System.arraycopy(data, 0, ndefMessage, 14 + i * 16, rest);
                                    }
                                }
                                typeLength = ndefMessage[1] & 0xFF;

                                payloadLength = ndefMessage[2] & 0xFF;

                                payloadType = new byte[typeLength];
                                System.arraycopy(ndefMessage, 3+offset, payloadType, 0, typeLength);
                                offset += typeLength;

                                ndefPayload = new byte[payloadLength];
                                System.arraycopy(ndefMessage, 3+offset, ndefPayload, 0, payloadLength);

                                // Display Header
                                MainActivity.header.setText("Current ESLOG temperature");

                                //  Display time
                                Date currentDate = new Date();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                                formattedTime = dateFormat.format(currentDate);
                                MainActivity.lastView.setText("Last reading: " + formattedTime);

                                //  Display ID
                                byte[] id_raw = MainActivity.s_MyTag.getId();
                                String id = Utility.Utility_ByteArrayToHexString(id_raw);
                                MainActivity.idView.setText(id);

                                //  Display Temperature
                                temperature_raw = new byte[6];
                                Utility.Utility_ReverseByteArray(ndefPayload);
                                System.arraycopy(ndefPayload, 18, temperature_raw, 0, 6);
                                Utility.Utility_ReverseByteArray(temperature_raw);
                                String temperature = new String(temperature_raw, StandardCharsets.UTF_8);
                                MainActivity.s_NfcContent.setText(temperature + "℃");

                                temp_array.add(temperature);
                                date_array.add(formattedTime);

                                MainActivity.showText("NDEF read");
                            }
                        }
                        else MainActivity.showText("Couldn't read data");
                    }
                    else MainActivity.showText("Data not read");
                    uTag.close();
                }
                else MainActivity.showText("Tag not connected");
            }
            catch (IOException e) {
                if(e instanceof TagLostException) MainActivity.showText("Tag lost!");
                else MainActivity.showText("Error!");
            }
        }
        else MainActivity.showText("Tag not detected");
    }

    public interface TemperatureChangeListener {
        void onTemperatureChanged(String newTemperature);
    }
}