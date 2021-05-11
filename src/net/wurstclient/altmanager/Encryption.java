/*
 * Copyright � 2014 - 2017 | Wurst-Imperium | All rights reserved.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.wurstclient.altmanager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.wurstclient.files.WurstFolders;

public final class Encryption
{
	private static final String CHARSET = "UTF-8";
	
	private final Cipher encryptCipher;
	private final Cipher decryptCipher;
	
	public Encryption()
	{
		KeyPair rsaKeyPair =
			getRsaKeyPair(WurstFolders.RSA.resolve("wurst_rsa.pub"),
				WurstFolders.RSA.resolve("wurst_rsa"));
		
		SecretKey aesKey =
			getAesKey(WurstFolders.MAIN.resolve("key"), rsaKeyPair);
		
		try
		{
			encryptCipher = Cipher.getInstance("AES/CFB8/NoPadding");
			encryptCipher.init(Cipher.ENCRYPT_MODE, aesKey,
				new IvParameterSpec(aesKey.getEncoded()));
			
			decryptCipher = Cipher.getInstance("AES/CFB8/NoPadding");
			decryptCipher.init(Cipher.DECRYPT_MODE, aesKey,
				new IvParameterSpec(aesKey.getEncoded()));
			
		}catch(GeneralSecurityException e)
		{
			throw new ReportedException(
				CrashReport.makeCrashReport(e, "Creating AES ciphers"));
		}
	}
	
	public void saveEncryptedFile(Path path, String content) throws IOException
	{
		Files.write(path, encrypt(content.getBytes(CHARSET)));
	}
	
	public String loadEncryptedFile(Path path) throws IOException
	{
		return new String(decrypt(Files.readAllBytes(path)), CHARSET);
	}
	
	public byte[] encrypt(byte[] bytes)
	{
		try
		{
			return Base64.getEncoder().encode(encryptCipher.doFinal(bytes));
		}catch(GeneralSecurityException e)
		{
			throw new ReportedException(
				CrashReport.makeCrashReport(e, "Encrypting bytes"));
		}
	}
	
	public byte[] decrypt(byte[] bytes)
	{
		try
		{
			return decryptCipher.doFinal(Base64.getDecoder().decode(bytes));
		}catch(GeneralSecurityException e)
		{
			throw new ReportedException(
				CrashReport.makeCrashReport(e, "Decrypting bytes"));
		}
	}
	
	private KeyPair getRsaKeyPair(Path publicFile, Path privateFile)
	{
		if(Files.notExists(publicFile) || Files.notExists(privateFile))
			return createRsaKeys(publicFile, privateFile);
		
		try
		{
			return loadRsaKeys(publicFile, privateFile);
		}catch(GeneralSecurityException | ReflectiveOperationException
			| IOException e)
		{
			System.err.println("Couldn't load RSA keypair!");
			e.printStackTrace();
			
			return createRsaKeys(publicFile, privateFile);
		}
	}
	
	private SecretKey getAesKey(Path path, KeyPair pair)
	{
		if(Files.notExists(path))
			return createAesKey(path, pair);
		
		try
		{
			return loadAesKey(path, pair);
		}catch(GeneralSecurityException | IOException e)
		{
			System.err.println("Couldn't load AES key!");
			e.printStackTrace();
			
			return createAesKey(path, pair);
		}
	}
	
	private KeyPair createRsaKeys(Path publicFile, Path privateFile)
	{
		try
		{
			System.out.println("Generating RSA keypair.");
			
			// generate keypair
			KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
			generator.initialize(1024);
			KeyPair pair = generator.generateKeyPair();
			
			KeyFactory factory = KeyFactory.getInstance("RSA");
			
			// save public key
			try(ObjectOutputStream out =
				new ObjectOutputStream(Files.newOutputStream(publicFile)))
			{
				RSAPublicKeySpec keySpec = factory.getKeySpec(pair.getPublic(),
					RSAPublicKeySpec.class);
				
				out.writeObject(keySpec.getModulus());
				out.writeObject(keySpec.getPublicExponent());
			}
			
			// save private key
			try(ObjectOutputStream out =
				new ObjectOutputStream(Files.newOutputStream(privateFile)))
			{
				RSAPrivateKeySpec keySpec = factory
					.getKeySpec(pair.getPrivate(), RSAPrivateKeySpec.class);
				
				out.writeObject(keySpec.getModulus());
				out.writeObject(keySpec.getPrivateExponent());
			}
			
			return pair;
			
		}catch(GeneralSecurityException | IOException e)
		{
			throw new ReportedException(
				CrashReport.makeCrashReport(e, "Creating RSA keypair"));
		}
	}
	
	private SecretKey createAesKey(Path path, KeyPair pair)
	{
		try
		{
			System.out.println("Generating AES key.");
			
			// generate key
			KeyGenerator keygen = KeyGenerator.getInstance("AES");
			keygen.init(128);
			SecretKey key = keygen.generateKey();
			
			// save key
			Cipher rsaCipher = Cipher.getInstance("RSA");
			rsaCipher.init(Cipher.ENCRYPT_MODE, pair.getPublic());
			Files.write(path, rsaCipher.doFinal(key.getEncoded()));
			
			return key;
			
		}catch(GeneralSecurityException | IOException e)
		{
			throw new ReportedException(
				CrashReport.makeCrashReport(e, "Creating AES key"));
		}
	}
	
	private KeyPair loadRsaKeys(Path publicFile, Path privateFile)
		throws GeneralSecurityException, ReflectiveOperationException,
		IOException
	{
		KeyFactory factory = KeyFactory.getInstance("RSA");
		
		// load public key
		PublicKey publicKey;
		try(ObjectInputStream in =
			new ObjectInputStream(Files.newInputStream(publicFile)))
		{
			publicKey = factory.generatePublic(new RSAPublicKeySpec(
				(BigInteger)in.readObject(), (BigInteger)in.readObject()));
		}
		
		// load private key
		PrivateKey privateKey;
		try(ObjectInputStream in =
			new ObjectInputStream(Files.newInputStream(privateFile)))
		{
			privateKey = factory.generatePrivate(new RSAPrivateKeySpec(
				(BigInteger)in.readObject(), (BigInteger)in.readObject()));
		}
		
		return new KeyPair(publicKey, privateKey);
	}
	
	private SecretKey loadAesKey(Path path, KeyPair pair)
		throws GeneralSecurityException, IOException
	{
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, pair.getPrivate());
		
		return new SecretKeySpec(cipher.doFinal(Files.readAllBytes(path)),
			"AES");
	}
}
