import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.crypto.SecretKey;

/**
 * 
 */

/**
 * @author georgos7
 *
 */
public class GetAccess {
	
	protected X509Certificate cert = null;
	protected String userpass;
	
	private char[] passcode;
	
	protected PrivateKey key;
	
	public GetAccess( String userpassd) {
		
		userpass = userpassd;
		
		String pcd = userpass+"codd";
		
		passcode = pcd.toCharArray();
	}
	
	public X509Certificate getAccessCertification(String args) {
		
		try {
			KeyStore keystore = KeyStore.getInstance("jks");
			
			keystore.load(new FileInputStream("confid/pirate.jks"), userpass.toCharArray());
			
		
			if(args.trim().equalsIgnoreCase("root")) {
				
			cert = 	(X509Certificate) keystore.getCertificateChain("userPrivateKey")[0];
			
			}else if(args.trim().equals("client")) {
				
				cert = 	(X509Certificate) keystore.getCertificateChain("userPrivateKey")[1];
		}else {
				
				System.out.println("no such entry");
				
			}
				
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cert;
		
	}

	protected PrivateKey getAccessKey() {
		
		try {
			KeyStore keystore = KeyStore.getInstance("jcks");
			
			keystore.load(new FileInputStream("confid/pirate.jks"), userpass.toCharArray());
			
			key = (PrivateKey) keystore.getKey("userPrivateKey", this.getCode());
			
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
			
		return key;
		
		
	}
	
	private char[] getCode() {
		
		return passcode;
	}
	
	public SecretKey getAsymmetrickey() {
		SecretKey kss = null;
		try {
			KeyStore keyst = KeyStore.getInstance("jceks");
			
			keyst.load(new FileInputStream("confid/pirate.jceks"), userpass.toCharArray());
			
			KeyStore.ProtectionParameter pparam = new KeyStore.PasswordProtection("winslygay".toCharArray());
			
			 KeyStore.SecretKeyEntry ent =(SecretKeyEntry) keyst.getEntry("asymmetrickey",	pparam );
			
			kss = ent.getSecretKey();
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException | UnrecoverableEntryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return kss;
	}

}
