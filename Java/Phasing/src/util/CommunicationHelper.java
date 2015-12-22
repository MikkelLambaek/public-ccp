package util;

import edu.biu.scapi.comm.Channel;
import edu.biu.scapi.comm.EncryptedChannel;
import edu.biu.scapi.comm.PlainChannel;
import edu.biu.scapi.comm.multiPartyComm.MultipartyCommunicationSetup;
import edu.biu.scapi.comm.multiPartyComm.SocketMultipartyCommunicationSetup;
import edu.biu.scapi.comm.twoPartyComm.LoadSocketParties;
import edu.biu.scapi.comm.twoPartyComm.PartyData;
import edu.biu.scapi.exceptions.SecurityLevelException;
import edu.biu.scapi.midLayer.symmetricCrypto.encryption.ScCTREncRandomIV;
import edu.biu.scapi.midLayer.symmetricCrypto.encryption.ScEncryptThenMac;
import edu.biu.scapi.midLayer.symmetricCrypto.mac.ScCbcMacPrepending;
import edu.biu.scapi.primitives.prf.AES;
import edu.biu.scapi.primitives.prf.bc.BcAES;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by Mikkel on 09-12-2015.
 */
public class CommunicationHelper {
    public static Map<PartyData, Map<String, Channel>> setChannel(String src) throws TimeoutException {
        //Prepare the parties list.
        LoadSocketParties loadParties = new LoadSocketParties("resources/properties/" + src + ".properties");

        List<PartyData> listOfParties = loadParties.getPartiesList();

        //Create the communication setup class.
//        MultipartyCommunicationSetup commSetup = new SocketMultipartyCommunicationSetup(listOfParties);
        MultipartyCommunicationSetup commSetup = new SocketMultipartyCommunicationSetup(listOfParties);

        //Request two chanels between me and each other party.
        HashMap<PartyData, Object> connectionsPerParty = new HashMap<PartyData, Object>();

        connectionsPerParty.put(listOfParties.get(1), 1);
        connectionsPerParty.put(listOfParties.get(2), 1);

        //Call the prepareForCommunication function to establish the connections within 2000000 milliseconds.
        //Returns the channels to the other parties.
        return commSetup.prepareForCommunication(connectionsPerParty, 2000000);
    }

    public static Channel getChannel(Map<PartyData, Map<String, Channel>> map, int n) {
        return ((HashMap<String,Channel>)map.values().toArray()[n]).get("0");
    }

    public static EncryptedChannel createSecureChannel(Channel ch, String partyOne, String partyTwo) {
        ScCTREncRandomIV enc = null;
        ScCbcMacPrepending cbcMac = null;
        try {
            // first, we set the encryption object

            // You could generate the key here and then somehow send it to the other party so the other party uses the same secret key
            // SecretKey encKey = SecretKeyGeneratorUtil.generateKey("AES");
            //Instead, we use a secretKey that has already been agreed upon by both parties:
            byte[] aesFixedKey = new byte[16];
            String[] temp = new String[16];
            for (String line : Files.readAllLines(Paths.get("resources/keys/" + partyOne + partyTwo + "AESKey.txt"))) {
                temp = line.split(", ");
                for (int i = 0; i < aesFixedKey.length; i++) {
                    aesFixedKey[i] = Byte.parseByte(temp[i]);
                }

            }
//            System.exit(1);
//            = new byte[]{-61, -19, 106, -97, 106, 40, 52, -64, -115, -19, -87, -67, 98, 102, 16, 21};
            SecretKey aesKey = new SecretKeySpec(aesFixedKey, "AES");

            AES encryptAes = new BcAES();
            encryptAes.setKey(aesKey);

            // create encryption object from PRP
            enc = new ScCTREncRandomIV(encryptAes);

            // second, we create the mac object
            AES macAes = new BcAES();

            macAes.setKey(aesKey);
            // create Mac object from PRP
            cbcMac = new ScCbcMacPrepending(macAes);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create the encrypt-then-mac object using encryption and authentication objects.
        ScEncryptThenMac encThenMac = null;
        encThenMac = new ScEncryptThenMac(enc, cbcMac);

        //Decorate the Plain TCP Channel with the authentication
        EncryptedChannel secureChannel = null;
        try {
            secureChannel = new EncryptedChannel((PlainChannel)ch, encThenMac);
        } catch (SecurityLevelException e) {
            // This exception will not happen since we chose a Mac that meets the Security Level requirements
            e.printStackTrace();
        }

        return secureChannel;
    }
}
